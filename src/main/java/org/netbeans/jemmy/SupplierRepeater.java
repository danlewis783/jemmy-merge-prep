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

import java.awt.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SupplierRepeater<R> {
    private static final String NO_WAITING_ALLOWED_ON_EDT = "no waiting allowed on EDT";
    private static final Logger logger = LoggerFactory.getLogger(SupplierRepeater.class);
    private final Supplier<R> supplier;
    private long startTime;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;

    private SupplierRepeater(Supplier<R> supplier, TimeoutKey waitKey, TimeoutKey waitDelta) {
        this.supplier = supplier;
        this.startTime = System.currentTimeMillis();
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier) {
        return new SupplierRepeater<>(supplier, TimeoutKey.Waiter_WaitingTime, TimeoutKey.Waiter_TimeDelta);
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier, TimeoutKey waitKey) {
        return new SupplierRepeater<>(supplier, waitKey, TimeoutKey.Waiter_TimeDelta);
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new SupplierRepeater<>(supplier, waitKey, waitDelta);
    }

    public static void waitFor(BooleanSupplier condition) {
        waitFor(condition, TimeoutKey.Waiter_WaitingTime);
    }

    public static void waitFor(BooleanSupplier condition, TimeoutKey waitKey) {
        waitFor(condition, waitKey, TimeoutKey.Waiter_TimeDelta);
    }

    public static void waitFor(BooleanSupplier condition, TimeoutKey waitKey, TimeoutKey waitDelta) {
        on(() -> condition.getAsBoolean() ? Boolean.TRUE : null, waitKey, waitDelta)
                .runUntilNotNull();
    }

    public R runUntilNotNull() {
        if (EventQueue.isDispatchThread()) {
            throw new RuntimeException(NO_WAITING_ALLOWED_ON_EDT);
        }

        long delta = Timeouts.get(waitDelta);
        long wait = Timeouts.get(waitKey);
        if (delta > wait) {
            throw new IllegalStateException(String.format(
                    "delta timeout \"%s\" (%d ms) greater than wait timeout \"%s\" (%d ms)",
                    waitDelta, delta, waitKey, wait));
        }

        startTime = System.currentTimeMillis();

        logger.debug("waiting for timeout \"{}\" ({} ms) to elapse", waitKey, wait);

        R r;

        while ((r = supplier.get()) == null) {
            Timeouts.sleep(waitDelta);
            Timeouts.check(waitKey, startTime);
        }

        return r;
    }
}
