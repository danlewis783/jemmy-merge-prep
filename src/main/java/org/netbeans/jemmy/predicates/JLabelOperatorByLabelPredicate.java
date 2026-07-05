package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JLabelOperatorByLabelPredicate implements Predicate<JLabelOperator> {
    private final StringComparator comparator;
    private final String label;

    public JLabelOperatorByLabelPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(JLabelOperator jLabelOp) {
        return comparator.equals(jLabelOp.getText(), label);
    }
}
