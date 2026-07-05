
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.FrameOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class FrameOperatorShowingByTitlePredicate<T extends FrameOperator> implements Predicate<T> {
    private final StringComparator comparator;
    private final String title;

    public FrameOperatorShowingByTitlePredicate(String title, StringComparator comparator) {
        this.title = title;
        this.comparator = comparator;
    }

    @Override
    public boolean test(T frameOp) {
        return frameOp.isShowing() && comparator.equals((frameOp).getTitle(), title);
    }
}
