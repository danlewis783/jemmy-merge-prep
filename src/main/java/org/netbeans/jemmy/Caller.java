
package org.netbeans.jemmy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class Caller<T> implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Caller.class);
    private final Callable<T> callable;
    private final CountDownLatch endGate;
    private final AtomicReference<Exception> exception;
    private final AtomicReference<T> result;
    private final CountDownLatch startGate;
    private final AtomicBoolean hasRun;

    private Caller(Callable<T> callable) {
        this.callable = callable;
        exception = new AtomicReference<>();
        result = new AtomicReference<>();
        startGate = new CountDownLatch(1);
        endGate = new CountDownLatch(1);
        hasRun = new AtomicBoolean(false);
    }

    @Override
    public final void run() {
        if (! hasRun.compareAndSet(false, true)) {
            throw new IllegalStateException("attempted to re-run Caller");
        }
        try {
            startGate.countDown();

            if (result.get() != null) {
                throw new IllegalStateException("result already non-null");
            } else {
                T call = callable.call();
                if (!result.compareAndSet(null, call)) {
                    throw new IllegalStateException("result already non-null");
                }
            }


        } catch (Exception e) {
            if (!exception.compareAndSet(null, e)) {
                throw new IllegalStateException("exception already thrown");
            }
        } finally {
            endGate.countDown();
        }
    }

    public CountDownLatch getStartGate() {
        return startGate;
    }

    public CountDownLatch getEndGate() {
        return endGate;
    }

    public Callable<T> getCallable() {
        return callable;
    }

    public T getResult() {
        return result.get();
    }

    public Exception getException() {
        return exception.get();
    }

    public static <Y> Caller<Y> of(Callable<Y> callable) {
        return new Caller<>(callable);
    }
}
