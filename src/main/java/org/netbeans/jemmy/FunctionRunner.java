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

import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

/**
 * Runs a {@link Function} on the Jemmy action thread; see {@link ActionRunner} for the
 * timeout, cancellation, and exception-capture semantics shared by all runners.
 */
public final class FunctionRunner<T, R> {
    private final Function<T, R> function;
    private final ActionRunner<R> runner = new ActionRunner<>();

    private FunctionRunner(Function<T, R> function) {
        this.function = function;
    }

    public static <T, R> FunctionRunner<T, R> on(Function<T, R> function) {
        return new FunctionRunner<>(function);
    }

    public @Nullable Throwable getThrowable() {
        return runner.getThrowable();
    }

    public @Nullable R submitAndGetDefaultTimeout(@Nullable T t) throws InterruptedException {
        return submitAndGet(t, TimeoutKey.ActionProducer_MaxActionTime);
    }

    public @Nullable R submitAndGet(@Nullable T t, TimeoutKey timeoutKey) throws InterruptedException {
        return runner.submitAndGet(() -> function.apply(t), timeoutKey);
    }

    public void run(@Nullable T t) {
        runner.submitLater(() -> function.apply(t));
    }
}
