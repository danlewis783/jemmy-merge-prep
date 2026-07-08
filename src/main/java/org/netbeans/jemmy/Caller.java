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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.jspecify.annotations.Nullable;

public final class Caller<T> implements Runnable {
    private final Callable<T> callable;
    private final CountDownLatch endGate;
    private final AtomicReference<@Nullable Exception> exception;
    private final AtomicReference<@Nullable T> result;
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
    public void run() {
        if (!hasRun.compareAndSet(false, true)) {
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

    public @Nullable T getResult() {
        return result.get();
    }

    public @Nullable Exception getException() {
        return exception.get();
    }

    public static <Y> Caller<Y> of(Callable<Y> callable) {
        return new Caller<>(callable);
    }
}
