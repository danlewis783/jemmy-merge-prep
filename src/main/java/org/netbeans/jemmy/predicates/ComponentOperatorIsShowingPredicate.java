package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class ComponentOperatorIsShowingPredicate implements Predicate<ComponentOperator> {
    private final boolean visibility;

    public ComponentOperatorIsShowingPredicate(boolean visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean test(ComponentOperator componentOp) {
        return componentOp.isShowing() == visibility;
    }
}
