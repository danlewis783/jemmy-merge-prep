package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JComponentOperator;

public final class JComponentOperatorVisiblePredicate implements Predicate<JComponentOperator> {
    private final boolean expectedVisibility;

    public JComponentOperatorVisiblePredicate(boolean expectedVisibility) {
        this.expectedVisibility = expectedVisibility;
    }

    @Override
    public boolean test(JComponentOperator jComponentOp) {
        return jComponentOp.isVisible() == expectedVisibility;
    }
}
