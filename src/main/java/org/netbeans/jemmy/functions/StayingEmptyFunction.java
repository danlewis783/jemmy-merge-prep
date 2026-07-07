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
package org.netbeans.jemmy.functions;

import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StayingEmptyFunction implements Function<Void, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(StayingEmptyFunction.class);
    private static final ExecutorService JEMMY_STAYING_EMPTY_SERVICE =
            Executors.newSingleThreadExecutor(new ThreadFactory() {
                final AtomicLong count = new AtomicLong(0);

                @Override
                public Thread newThread(Runnable runnable) {
                    Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                    thread.setUncaughtExceptionHandler(
                            (t, e) -> logger.warn("uncaught exception in thread {}", t.getName(), e));
                    thread.setName(String.format("jemmy-staying-empty-%d", count.getAndIncrement()));
                    return thread;
                }
            });
    private static final TimeoutKey DELTA_KEY = TimeoutKey.Waiter_TimeDelta;
    private static final TimeoutKey WAIT_KEY = TimeoutKey.Waiter_WaitingTime;
    private final long emptyTime;
    private final long delta;
    private final long wait;

    public StayingEmptyFunction(long emptyTime) {
        this.delta = Timeouts.get(DELTA_KEY);
        this.wait = Timeouts.get(WAIT_KEY);
        this.emptyTime = Math.max(delta, emptyTime);
    }

    @Override
    public @Nullable Boolean apply(Void obj) {
        long startTime = System.currentTimeMillis();
        UntilEventObserved callable = new UntilEventObserved(emptyTime, delta);
        Future<Boolean> future = JEMMY_STAYING_EMPTY_SERVICE.submit(callable);
        boolean isEmpty;
        try {
            isEmpty = future.get(wait, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            throw new TimeoutExpiredException(
                    String.format(
                            "timeout \"%s\" (%d ms) exceeded after (%d ms)",
                            WAIT_KEY, wait, (System.currentTimeMillis() - startTime)),
                    e);
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("", e);
            throw new RuntimeException(e);
        } finally {
            if (!future.isDone()) {
                if (!future.cancel(true)) {
                    logger.warn("could not be cancelled");
                }
            }
        }

        if (isEmpty && (System.currentTimeMillis() - startTime <= wait)) {
            return true;
        }

        return null;
    }

    private static final class UntilEventObserved implements Callable<Boolean> {
        private static final Logger logger = LoggerFactory.getLogger(UntilEventObserved.class);
        public static final long MAX_RECOMMENDED_DELTA = 100L;
        private final long checkDelta;
        private final long emptyTime;

        /**
         * Note: If checkDelta >= emptyTime, then the runnable will only run once.
         *
         * @param emptyTime  positive number of milliseconds the system event queue should stay empty
         * @param checkDelta positive number of milliseconds to wait between polls of system event queue
         */
        private UntilEventObserved(long emptyTime, long checkDelta) {
            if (emptyTime <= 0 || checkDelta <= 0) {
                throw new IllegalArgumentException();
            }
            if (emptyTime < checkDelta) {
                logger.warn("emptyTime {} should be >= checkDelta {}", emptyTime, checkDelta);
            }
            if (checkDelta >= MAX_RECOMMENDED_DELTA) {
                logger.warn("using a large checkDelta {} >= recommended {}", checkDelta, MAX_RECOMMENDED_DELTA);
            }
            this.emptyTime = emptyTime;
            this.checkDelta = checkDelta;
        }

        @Override
        public Boolean call() throws Exception {
            long startTime = System.currentTimeMillis();
            while (true) {
                boolean eventFound =
                        Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() != null;
                if (eventFound) {
                    return Boolean.FALSE;
                }

                if (((System.currentTimeMillis() - startTime) + checkDelta) >= emptyTime) {
                    break;
                }

                Thread.sleep(checkDelta);
            }
            return Boolean.TRUE;
        }
    }
}
