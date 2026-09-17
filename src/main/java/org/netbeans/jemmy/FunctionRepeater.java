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
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

/**
 * Polls a {@link Function} of a fixed argument until it produces a non-null value; see
 * {@link Repeater} for the loop, guard, and timeout semantics shared by all repeaters.
 */
public final class FunctionRepeater<T, R> {
    private final Function<T, R> function;
    private final TimeoutKey waitKey;
    private final TimeoutKey waitDelta;
    private final Object describedTarget;
    private final @Nullable Component diagnosticComponent;

    private FunctionRepeater(
            Function<T, R> function,
            TimeoutKey waitKey,
            TimeoutKey waitDelta,
            Object describedTarget,
            @Nullable Component diagnosticComponent) {
        this.function = function;
        this.waitKey = waitKey;
        this.waitDelta = waitDelta;
        this.describedTarget = describedTarget;
        this.diagnosticComponent = diagnosticComponent;
    }

    public static <T, R> FunctionRepeater<T, R> on(Function<T, R> function) {
        return new FunctionRepeater<>(
                function, TimeoutKey.Waiter_WaitingTime, TimeoutKey.Waiter_TimeDelta, function, null);
    }

    public static <T, R> FunctionRepeater<T, R> on(Function<T, R> function, TimeoutKey waitKey) {
        return new FunctionRepeater<>(function, waitKey, TimeoutKey.Waiter_TimeDelta, function, null);
    }

    public static <T, R> FunctionRepeater<T, R> on(Function<T, R> function, TimeoutKey waitKey, TimeoutKey waitDelta) {
        return new FunctionRepeater<>(function, waitKey, waitDelta, function, null);
    }

    /** Uses the supplied object's lazy {@code toString()} only when this wait times out. */
    public FunctionRepeater<T, R> describedAs(Object target) {
        return new FunctionRepeater<>(
                function, waitKey, waitDelta, Objects.requireNonNull(target, "target"), diagnosticComponent);
    }

    /** Captures the component whose state is being polled if this wait times out. */
    public FunctionRepeater<T, R> diagnosing(Component component) {
        return new FunctionRepeater<>(
                function, waitKey, waitDelta, describedTarget, Objects.requireNonNull(component, "component"));
    }

    public R runUntilNotNull(@Nullable T t) {
        AtomicReference<@Nullable R> result = new AtomicReference<>();
        Repeater.repeatUntilTrue(
                () -> {
                    R r = function.apply(t);
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
