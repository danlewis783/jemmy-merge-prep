package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class AbstractButtonOperatorByTextPredicate<T extends AbstractButtonOperator> implements Predicate<T> {
    private final StringComparator comparator;
    private final String expectedText;

    public AbstractButtonOperatorByTextPredicate(String expectedText, StringComparator comparator) {
        this.expectedText = expectedText;
        this.comparator = comparator;
    }

    @Override
    public boolean test(T absButtonOp) {
        return comparator.equals((absButtonOp).getText(), expectedText);
    }
}
