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

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * Polls a {@link BooleanSupplier} until it reports true; see {@link Repeater} for the loop,
 * guard, and timeout semantics shared by all repeaters.
 */
public final class BooleanSupplierRepeater {
    private final BooleanSupplier booleanSupplier;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;
    private final Object describedTarget;

    private BooleanSupplierRepeater(
            BooleanSupplier booleanSupplier, TimeoutKey waitKey, TimeoutKey waitDelta, Object describedTarget) {
        this.booleanSupplier = booleanSupplier;
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
        this.describedTarget = describedTarget;
    }

    public static BooleanSupplierRepeater on(BooleanSupplier booleanSupplier) {
        return new BooleanSupplierRepeater(
                booleanSupplier, TimeoutKey.Waiter_WaitingTime, TimeoutKey.Waiter_TimeDelta, booleanSupplier);
    }

    public static BooleanSupplierRepeater on(BooleanSupplier booleanSupplier, TimeoutKey waitKey) {
        return new BooleanSupplierRepeater(booleanSupplier, waitKey, TimeoutKey.Waiter_TimeDelta, booleanSupplier);
    }

    public static BooleanSupplierRepeater on(
            BooleanSupplier booleanSupplier, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new BooleanSupplierRepeater(booleanSupplier, waitKey, waitDelta, booleanSupplier);
    }

    /** Uses the supplied object's lazy {@code toString()} only when this wait times out. */
    public BooleanSupplierRepeater describedAs(Object target) {
        return new BooleanSupplierRepeater(
                booleanSupplier, waitKey, waitDelta, Objects.requireNonNull(target, "target"));
    }

    public static void waitFor(BooleanSupplier condition) {
        waitFor(condition, TimeoutKey.Waiter_WaitingTime);
    }

    public static void waitFor(BooleanSupplier condition, TimeoutKey waitKey) {
        waitFor(condition, waitKey, TimeoutKey.Waiter_TimeDelta);
    }

    public static void waitFor(BooleanSupplier condition, TimeoutKey waitKey, TimeoutKey waitDelta) {
        Repeater.repeatUntilTrue(condition, waitKey, waitDelta, condition);
    }

    public void runUntilTrue() {
        Repeater.repeatUntilTrue(booleanSupplier, waitKey, waitDelta, describedTarget);
    }
}
