/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The engine behind the public runners ({@link FunctionRunner}, {@link SupplierRunner},
 * {@link RunnableRunner}): the single Jemmy action thread plus what
 * timeout, cancellation, and exception capture mean for work submitted to it. Keeping every
 * runner on this one executor serializes background actions first-in-first-out, so two
 * actions never drive the real mouse and keyboard at the same time.
 */
final class ActionRunner<R> {
    private static final Logger logger = LoggerFactory.getLogger(ActionRunner.class);
    private static final ThreadLocal<ActionScope> CURRENT_ACTION_SCOPE = new ThreadLocal<>();
    private static final ExecutorService JEMMY_ACTION_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final AtomicLong count = new AtomicLong(0);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setUncaughtExceptionHandler(
                    (t, e) -> logger.warn("uncaught exception in thread {}", t.getName(), e));
            thread.setName(String.format("jemmy-action-%d", count.getAndIncrement()));
            return thread;
        }
    });

    /** No-blocking actions submitted but not yet finished, guarded by itself. */
    private static final Set<PendingAction> PENDING_ACTIONS = new LinkedHashSet<>();

    private final AtomicReference<@Nullable Throwable> throwable = new AtomicReference<>();

    @Nullable
    Throwable getThrowable() {
        return throwable.get();
    }

    @Nullable
    R submitAndGet(Callable<@Nullable R> work, TimeoutKey timeoutKey) throws InterruptedException {
        // same fail-fast contract as Repeater: blocking on the EDT would park the thread the
        // submitted action needs to make progress, freezing the UI for the whole budget
        if (EventQueue.isDispatchThread()) {
            throw new RuntimeException("no waiting allowed on EDT");
        }
        throwable.set(null);
        ActionScope actionScope = new ActionScope();
        JemmyDiagnostics.Recording recording = JemmyDiagnostics.currentRecording();
        Future<R> laFutura = JEMMY_ACTION_SERVICE.submit(() -> {
            CURRENT_ACTION_SCOPE.set(actionScope);
            JemmyDiagnostics.Recording previous = JemmyDiagnostics.useRecording(recording);
            try {
                return work.call();
            } finally {
                JemmyDiagnostics.useRecording(previous);
                CURRENT_ACTION_SCOPE.remove();
            }
        });
        long timeout = Timeouts.get(timeoutKey);
        long startTime = System.currentTimeMillis();
        try {
            return laFutura.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throwable.set(cause);
            if (cause instanceof JemmyException) {
                throw (JemmyException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new JemmyException("Throwable captured by action runner", cause);
        } catch (TimeoutException e) {
            throwable.set(e);
            // capture before the finally-block cancel below interrupts the action: the
            // jemmy-action stack in the diagnostics shows where the action was stuck
            throw JemmyDiagnostics.timeoutFailure(
                    String.format(
                            "timeout \"%s\" (%d ms) exceeded after (%d ms)",
                            timeoutKey, timeout, (System.currentTimeMillis() - startTime)),
                    timeoutKey,
                    timeout,
                    "Jemmy action to complete",
                    null,
                    e);
        } finally {
            // Future.cancel(true) only requests an interrupt; it does not wait for the
            // worker to observe it. Cancel pending EDT callers first so none can start
            // after this method has already reported that the action was abandoned.
            if (!laFutura.isDone()) {
                actionScope.cancelPendingCallers();
                if (!laFutura.cancel(true)) {
                    logger.warn("abandoned action could not be cancelled");
                }
            }
        }
    }

    void submitLater(Runnable work) {
        JemmyDiagnostics.Recording recording = JemmyDiagnostics.currentRecording();
        Thread invoker = Thread.currentThread();
        Throwable submission = new Throwable("Asynchronous Jemmy action submitted here by "
                + invoker.getName() + " [id=" + invoker.getId() + "]");
        PendingAction pending = new PendingAction(submission);
        synchronized (PENDING_ACTIONS) {
            PENDING_ACTIONS.add(pending);
        }
        try {
            JEMMY_ACTION_SERVICE.execute(() -> {
                if (!pending.start()) {
                    return;
                }
                throwable.set(null);
                JemmyDiagnostics.Recording previous = JemmyDiagnostics.useRecording(recording);
                try {
                    work.run();
                } catch (Throwable failure) {
                    throwable.set(failure);
                    if (recording == null || !recording.recordAction(failure, submission)) {
                        // Work that outlives its recording stays visible without contaminating
                        // the next test. An unobserved action must never disappear silently,
                        // and the submission stack names the caller that left it behind.
                        failure.addSuppressed(submission);
                        logger.warn("exception in no-blocking action", failure);
                    }
                    if (failure instanceof VirtualMachineError || failure instanceof ThreadDeath) {
                        throw (Error) failure;
                    }
                } finally {
                    JemmyDiagnostics.useRecording(previous);
                    pending.finish();
                }
            });
        } catch (RuntimeException rejected) {
            removePending(pending);
            throw rejected;
        }
    }

    /**
     * Waits until every no-blocking action submitted so far has finished or been cancelled.
     *
     * @return {@code false} if some are still queued or running when the timeout elapses
     */
    static boolean awaitNoBlockingActions(long timeoutMillis) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        synchronized (PENDING_ACTIONS) {
            while (!PENDING_ACTIONS.isEmpty()) {
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0L) {
                    return false;
                }
                TimeUnit.NANOSECONDS.timedWait(PENDING_ACTIONS, remaining);
            }
            return true;
        }
    }

    /**
     * Cancels every unfinished no-blocking action: queued ones never start, and a running one
     * is interrupted. An interrupted action ends as soon as it next waits or sleeps; use
     * {@link #awaitNoBlockingActions(long)} to wait for that.
     *
     * @return where each cancelled action was submitted, oldest first
     */
    static List<Throwable> cancelNoBlockingActions() {
        List<PendingAction> cancelled;
        synchronized (PENDING_ACTIONS) {
            cancelled = new ArrayList<>(PENDING_ACTIONS);
        }
        List<Throwable> submissions = new ArrayList<>();
        for (PendingAction pending : cancelled) {
            if (pending.cancel()) {
                // it never started, so it will never finish on its own
                removePending(pending);
            }
            submissions.add(pending.submission);
        }
        // only once every action is marked: an interrupted action can end, and the executor
        // start the next queued one, before that one would otherwise have been marked
        for (PendingAction pending : cancelled) {
            pending.interruptIfRunning();
        }
        return submissions;
    }

    private static void removePending(PendingAction pending) {
        synchronized (PENDING_ACTIONS) {
            PENDING_ACTIONS.remove(pending);
            PENDING_ACTIONS.notifyAll();
        }
    }

    static boolean registerPendingCaller(Caller<?> caller) {
        ActionScope actionScope = CURRENT_ACTION_SCOPE.get();
        return actionScope == null || actionScope.register(caller);
    }

    static void unregisterPendingCaller(Caller<?> caller) {
        ActionScope actionScope = CURRENT_ACTION_SCOPE.get();
        if (actionScope != null) {
            actionScope.unregister(caller);
        }
    }

    /** One no-blocking action between submission and completion. */
    private static final class PendingAction {
        final Throwable submission;
        private @Nullable Thread runner;
        private boolean started;
        private boolean cancelled;

        PendingAction(Throwable submission) {
            this.submission = submission;
        }

        /** Returns false when the action was cancelled before it could start. */
        synchronized boolean start() {
            if (cancelled) {
                return false;
            }
            started = true;
            runner = Thread.currentThread();
            return true;
        }

        void finish() {
            synchronized (this) {
                runner = null;
            }
            removePending(this);
        }

        /** Stops the action from starting; returns true when it had not started yet. */
        synchronized boolean cancel() {
            cancelled = true;
            return !started;
        }

        synchronized void interruptIfRunning() {
            if (runner != null) {
                runner.interrupt();
            }
        }
    }

    /**
     * Tracks EDT work posted by one timed action. Synchronization makes cancellation and
     * registration atomic with respect to each other, so a timeout cannot miss a caller that is
     * about to be queued.
     */
    private static final class ActionScope {
        private final Set<Caller<?>> pendingCallers = new HashSet<>();
        private boolean cancelled;

        synchronized boolean register(Caller<?> caller) {
            if (cancelled) {
                caller.cancel();
                return false;
            }
            pendingCallers.add(caller);
            return true;
        }

        synchronized void unregister(Caller<?> caller) {
            pendingCallers.remove(caller);
        }

        synchronized void cancelPendingCallers() {
            cancelled = true;
            for (Caller<?> caller : pendingCallers) {
                caller.cancel();
            }
        }
    }
}
