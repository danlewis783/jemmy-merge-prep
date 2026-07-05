package org.netbeans.jemmy;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class FunctionRepeater<F, T> {
    private static final String NO_WAITING_ALLOWED_ON_EDT = "no waiting allowed on EDT";
    private static final Logger logger = LoggerFactory.getLogger(FunctionRepeater.class);
    private final Function<F, T> function;
    private long startTime;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;

    private FunctionRepeater(Function<F, T> function, TimeoutKey waitKey, TimeoutKey waitDelta) {
        this.function = function;
        this.startTime = System.currentTimeMillis();
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
    }

    public static <F, T> FunctionRepeater<F, T> on(Function<F, T> function) {
        return new FunctionRepeater<>(function, TimeoutKey.Waiter_WaitingTime, TimeoutKey.Waiter_TimeDelta);
    }

    public static <F, T> FunctionRepeater<F, T> on(Function<F, T> function, TimeoutKey waitKey) {
        return new FunctionRepeater<>(function, waitKey, TimeoutKey.Waiter_TimeDelta);
    }

    public static <F, T> FunctionRepeater<F, T> on(Function<F, T> function, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new FunctionRepeater<>(function, waitKey, waitDelta);
    }

    public T runUntilNotNull(@Nullable F f) throws InterruptedException {
        if (EventQueue.isDispatchThread()) {
            throw new RuntimeException(NO_WAITING_ALLOWED_ON_EDT);
        }

        long delta = Timeouts.get(waitDelta);
        long wait = Timeouts.get(waitKey);
        if (delta > wait) {
            throw new IllegalStateException(String.format("delta timeout \"%s\" (%d ms) greater than wait timeout \"%s\" (%d ms)",
                    waitDelta, delta, waitKey, wait));
        }

        startTime = System.currentTimeMillis();

        logger.debug("waiting for timeout \"{}\" ({} ms) to elapse", waitKey, wait);

        T t;

        while ((t = function.apply(f)) == null) {
            Timeouts.sleep(waitDelta);
            Timeouts.check(waitKey, startTime);
        }

        return t;
    }
}
