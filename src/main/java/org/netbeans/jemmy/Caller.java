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

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapts a {@link Callable} so that it can be run through the Swing Event Queue: a runnable
 * whose start and completion can be awaited, and which captures rather than throws anything
 * the work raises. Capturing must cover {@link Throwable}, not just {@link Exception}: an
 * Error that escaped into the InvocationEvent would be recorded only after the end gate has
 * already released the waiting thread, which could then miss it entirely.
 *
 * <p>A waiting thread that gives up (timeout, interrupt) can {@link #cancel()} the caller:
 * whichever of {@code cancel()} and {@link #run()} claims the state first wins, so abandoned
 * work either never starts or was already past recall — a stale InvocationEvent dispatched
 * long after its awaiter left no-ops instead of replaying the side effects.
 *
 * @param <R> the type to be returned from the callable
 */
final class Caller<R> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Caller.class);

    private enum State { FRESH, RUNNING, CANCELLED }

    private final Callable<R> callable;
    private final CountDownLatch endGate;
    private final AtomicReference<@Nullable Throwable> throwable;
    private final AtomicReference<@Nullable R> result;
    private final CountDownLatch startGate;
    private final AtomicReference<State> state;

    private Caller(Callable<R> callable) {
        this.callable = callable;
        throwable = new AtomicReference<>();
        result = new AtomicReference<>();
        startGate = new CountDownLatch(1);
        endGate = new CountDownLatch(1);
        state = new AtomicReference<>(State.FRESH);
    }

    /**
     * Marks the work as abandoned by its awaiter.
     *
     * @return true when the work had not started and will now never run; false when it already
     *     started and cannot be recalled
     */
    boolean cancel() {
        return state.compareAndSet(State.FRESH, State.CANCELLED);
    }

    @Override
    public void run() {
        if (!state.compareAndSet(State.FRESH, State.RUNNING)) {
            if (state.get() == State.CANCELLED) {
                logger.debug("skipping caller abandoned by its awaiter");
                return;
            }
            throw new IllegalStateException("attempted to re-run Caller");
        }
        try {
            startGate.countDown();

            if (result.get() != null) {
                throw new IllegalStateException("result already non-null");
            } else {
                R call = callable.call();
                if (!result.compareAndSet(null, call)) {
                    throw new IllegalStateException("result already non-null");
                }
            }

        } catch (Throwable t) {
            if (!throwable.compareAndSet(null, t)) {
                throw new IllegalStateException("throwable already captured");
            }
        } finally {
            endGate.countDown();
        }
    }

    CountDownLatch getStartGate() {
        return startGate;
    }

    CountDownLatch getEndGate() {
        return endGate;
    }

    @Nullable
    R getResult() {
        return result.get();
    }

    @Nullable
    Throwable getThrowable() {
        return throwable.get();
    }

    static <RR> Caller<RR> of(Callable<RR> callable) {
        return new Caller<>(callable);
    }
}
