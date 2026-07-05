package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class ComponentOperatorIsEnabledPredicate<T extends ComponentOperator> implements Predicate<T> {
    public ComponentOperatorIsEnabledPredicate() {}

    @Override
    public boolean test(T compOp) {
        return compOp.isEnabled();
    }
}
