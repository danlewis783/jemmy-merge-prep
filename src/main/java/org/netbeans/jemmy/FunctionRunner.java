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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FunctionRunner<T, R> {
    private static final Logger logger = LoggerFactory.getLogger(FunctionRunner.class);
    private static final ExecutorService JEMMY_ACTION_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactory() {
        final AtomicLong count = new AtomicLong(0);

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setUncaughtExceptionHandler(
                    (t, e) -> logger.warn("uncaught exception in thread {}", t.getName(), e));
            thread.setName(String.format("jemmy-function-waiter-%d", count.getAndIncrement()));
            return thread;
        }
    });
    private final Function<T, R> function;
    private final AtomicReference<@Nullable Throwable> throwable;

    private FunctionRunner(Function<T, R> function) {
        this.function = function;
        this.throwable = new AtomicReference<>();
    }

    public static <T, R> FunctionRunner<T, R> on(Function<T, R> function) {
        return new FunctionRunner<>(function);
    }

    public @Nullable Throwable getThrowable() {
        return throwable.get();
    }

    public @Nullable R submitAndGetDefaultTimeout(@Nullable T t) throws InterruptedException {
        return submitAndGet(t, TimeoutKey.ActionProducer_MaxActionTime);
    }

    public @Nullable R submitAndGet(@Nullable T t, TimeoutKey timeoutKey) throws InterruptedException {
        Future<R> future = JEMMY_ACTION_SERVICE.submit(() -> function.apply(t));
        long timeout = Timeouts.get(timeoutKey);
        long startTime = System.currentTimeMillis();
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutExpiredException) {
                throw (TimeoutExpiredException) cause;
            }
            logger.warn("caught ExecutionException running function", e);
            throwable.set(cause);
        } catch (TimeoutException e) {
            throwable.set(e);
            throw new TimeoutExpiredException(
                    String.format(
                            "timeout \"%s\" (%d ms) exceeded after (%d ms)",
                            timeoutKey, timeout, (System.currentTimeMillis() - startTime)),
                    e);
        }

        return null;
    }

    public void run(@Nullable T t) {
        JEMMY_ACTION_SERVICE.execute(() -> function.apply(t));
    }
}
