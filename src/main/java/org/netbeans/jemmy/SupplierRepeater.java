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

import java.awt.Component;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Polls a {@link Supplier} until it produces a non-null value; see {@link Repeater} for the
 * loop, guard, and timeout semantics shared by all repeaters.
 */
public final class SupplierRepeater<R> {
    private final Supplier<R> supplier;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;
    private final Object describedTarget;
    private final @Nullable Component diagnosticComponent;

    private SupplierRepeater(
            Supplier<R> supplier,
            TimeoutKey waitKey,
            TimeoutKey waitDelta,
            Object describedTarget,
            @Nullable Component diagnosticComponent) {
        this.supplier = supplier;
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
        this.describedTarget = describedTarget;
        this.diagnosticComponent = diagnosticComponent;
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier) {
        return new SupplierRepeater<>(
                supplier, TimeoutKey.Waiter_WaitingTime, TimeoutKey.Waiter_TimeDelta, supplier, null);
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier, TimeoutKey waitKey) {
        return new SupplierRepeater<>(supplier, waitKey, TimeoutKey.Waiter_TimeDelta, supplier, null);
    }

    public static <R> SupplierRepeater<R> on(Supplier<R> supplier, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new SupplierRepeater<>(supplier, waitKey, waitDelta, supplier, null);
    }

    /**
     * Uses a meaningful object, such as a named predicate, to describe this wait if it times out.
     * Its {@code toString()} is evaluated only on the failure path.
     */
    public SupplierRepeater<R> describedAs(Object target) {
        return new SupplierRepeater<>(
                supplier, waitKey, waitDelta, Objects.requireNonNull(target, "target"), diagnosticComponent);
    }

    /** Captures the component whose hierarchy is being searched if this wait times out. */
    public SupplierRepeater<R> diagnosing(Component component) {
        return new SupplierRepeater<>(
                supplier, waitKey, waitDelta, describedTarget, Objects.requireNonNull(component, "component"));
    }

    public R runUntilNotNull() {
        AtomicReference<@Nullable R> result = new AtomicReference<>();
        Repeater.repeatUntilTrue(
                () -> {
                    R r = supplier.get();
                    result.set(r);
                    return r != null;
                },
                waitKey,
                waitDelta,
                describedTarget,
                diagnosticComponent);

        return Objects.requireNonNull(result.get());
    }
}
