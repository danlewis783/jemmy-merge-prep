package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JTextComponentOperatorByTextPredicate implements Predicate<JTextComponentOperator> {
    private final StringComparator comparator;
    private final String expectedText;

    public JTextComponentOperatorByTextPredicate(String expectedText, StringComparator comparator) {
        this.expectedText = expectedText;
        this.comparator = comparator;
    }

    @Override
    public boolean test(JTextComponentOperator jTextComponentOp) {
        return comparator.equals((jTextComponentOp).getText(), expectedText);
    }
}
