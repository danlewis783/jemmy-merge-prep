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
import java.util.HashSet;
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
        ActionScope actionScope = new ActionScope();
        Future<R> laFutura = JEMMY_ACTION_SERVICE.submit(() -> {
            CURRENT_ACTION_SCOPE.set(actionScope);
            try {
                return work.call();
            } finally {
                CURRENT_ACTION_SCOPE.remove();
            }
        });
        long timeout = Timeouts.get(timeoutKey);
        long startTime = System.currentTimeMillis();
        try {
            return laFutura.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutExpiredException) {
                throw (TimeoutExpiredException) cause;
            }
            throwable.set(cause);
        } catch (TimeoutException e) {
            throwable.set(e);
            // capture before the finally-block cancel below interrupts the action: the
            // jemmy-action stack in the diagnostics shows where the action was stuck
            throw new TimeoutExpiredException(
                    String.format(
                            "timeout \"%s\" (%d ms) exceeded after (%d ms)%n%s",
                            timeoutKey,
                            timeout,
                            (System.currentTimeMillis() - startTime),
                            WaitDiagnostics.capture()),
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

        return null;
    }

    void submitLater(Runnable work) {
        JEMMY_ACTION_SERVICE.execute(() -> {
            try {
                work.run();
            } catch (RuntimeException e) {
                // the submitter has already returned and cannot be told; log so the failure
                // is not lost, and keep the single worker thread alive for later actions
                logger.warn("exception in no-blocking action", e);
            }
        });
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
