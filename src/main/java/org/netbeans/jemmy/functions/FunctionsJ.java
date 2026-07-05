package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Predicate;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
        public T apply(F f) {
            return null;
        }
    }

    private static class IsAtomicBooleanTrueFunction implements Function<Void, Boolean> {
        private final AtomicBoolean atomicBoolean;

        public IsAtomicBooleanTrueFunction(AtomicBoolean atomicBoolean) {
            this.atomicBoolean = atomicBoolean;
        }

        @Override
        public Boolean apply(Void v) {
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
            return "Function wrapping " + predicate.toString();
        }
    }
}
