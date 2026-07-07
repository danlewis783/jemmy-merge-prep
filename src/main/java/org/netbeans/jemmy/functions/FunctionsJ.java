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
package org.netbeans.jemmy.functions;

import java.awt.Component;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

public final class FunctionsJ {
    private FunctionsJ() {}

    public static <T extends Component> Function<T, Boolean> forPredicate(Predicate<T> predicate) {
        return new PredicateFunction<>(predicate);
    }

    public static <F, T> Function<F, T> alwaysNull() {
        return new AlwaysNullFunction<>();
    }

    public static Function<Void, Boolean> isAtomicBooleanTrue(AtomicBoolean value) {
        return new IsAtomicBooleanTrueFunction(value);
    }

    private static class AlwaysNullFunction<F, T> implements Function<F, T> {
        @Override
        public @Nullable T apply(F f) {
            return null;
        }
    }

    private static class IsAtomicBooleanTrueFunction implements Function<Void, Boolean> {
        private final AtomicBoolean atomicBoolean;

        public IsAtomicBooleanTrueFunction(AtomicBoolean atomicBoolean) {
            this.atomicBoolean = atomicBoolean;
        }

        @Override
        public @Nullable Boolean apply(Void v) {
            return atomicBoolean.get() ? true : null;
        }
    }

    private static class PredicateFunction<T extends Component> implements Function<T, Boolean> {
        private final Predicate<T> predicate;

        public PredicateFunction(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public Boolean apply(T input) {
            return predicate.test(input);
        }

        @Override
        public String toString() {
            return "Function wrapping " + predicate;
        }
    }
}
