package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public final class DialogOperatorShowingByTitlePredicate implements Predicate<DialogOperator> {
    private final StringComparator comparator;
    private final String title;

    public DialogOperatorShowingByTitlePredicate(String title) {
        this(title, StringComparators.strict());
    }

    public DialogOperatorShowingByTitlePredicate(String title, StringComparator comparator) {
        this.title = title;
        this.comparator = comparator;
    }

    @Override
    public boolean test(DialogOperator dialogOp) {
        return dialogOp.isShowing() && comparator.equals((dialogOp).getTitle(), title);
    }
}
