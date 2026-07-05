package org.netbeans.jemmy;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.functions.FunctionsJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public final class FunctionRunner<F, T> {
    private static final Logger logger = LoggerFactory.getLogger(FunctionRunner.class);
    private static final ExecutorService JEMMY_ACTION_SERVICE =
            Executors.newSingleThreadExecutor(
                    new ThreadFactory() {
                        final AtomicLong count = new AtomicLong(0);

                        @Override
                        public Thread newThread(Runnable runnable) {
                            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                            thread.setUncaughtExceptionHandler((t, e) -> logger.warn("uncaught exception in thread " + t.getName(), e));
                            thread.setName(String.format("jemmy-function-waiter-%d", count.getAndIncrement()));
                            return thread;
                        }
                    });
    private final Function<F, T> function;
    private final AtomicReference<Throwable> throwable;

    private FunctionRunner(Function<F, T> function) {
        this.function = function;
        this.throwable = new AtomicReference<>();
    }

    public static <F, T> FunctionRunner<F, T> on(Function<F, T> function) {
        return new FunctionRunner<>(function);
    }

    public static <F extends Component> FunctionRunner<F, Boolean> on(Predicate<F> predicate) {
        return FunctionRunner.on(FunctionsJ.forPredicate(predicate));
    }

    public Throwable getThrowable() {
        return throwable.get();
    }

    public T submitAndGetDefaultTimeout(@Nullable F f) throws InterruptedException {
        return submitAndGet(f, TimeoutKey.ActionProducer_MaxActionTime);
    }

    public T submitAndGet(@Nullable F f, TimeoutKey timeoutKey) throws InterruptedException {
        Future<T> future = JEMMY_ACTION_SERVICE.submit(() -> function.apply(f));
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
            throw new TimeoutExpiredException(String.format(
                    "timeout \"%s\" (%d ms) exceeded after (%d ms)", timeoutKey, timeout, (System.currentTimeMillis() - startTime)), e);
        }

        return null;
    }

    public void run(@Nullable F f) throws InterruptedException {
        JEMMY_ACTION_SERVICE.execute(() -> function.apply(f));
    }
}
