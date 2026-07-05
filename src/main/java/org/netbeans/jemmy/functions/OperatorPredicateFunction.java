package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.operators.Operator;

public final class OperatorPredicateFunction<T extends Operator> implements Function<Void, Boolean> {
    private final T operator;
    private final Predicate<T> predicate;

    public OperatorPredicateFunction(Predicate<T> predicate, T operator) {
        this.predicate = predicate;
        this.operator = operator;
    }

    @Override
    public Boolean apply(Void obj) {
        return predicate.test(operator) ? true : null;
    }
}
