package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class ComponentOperatorIsVisiblePredicate<T extends ComponentOperator> implements Predicate<T> {
    private final boolean expectedVisibility;

    public ComponentOperatorIsVisiblePredicate(boolean expectedVisibility) {
        this.expectedVisibility = expectedVisibility;
    }

    @Override
    public boolean test(T compOp) {
        return compOp.isVisible() == expectedVisibility;
    }
}
