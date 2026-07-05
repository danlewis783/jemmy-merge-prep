package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import java.awt.*;

public final class DialogShowingByTitlePredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String title;

    public DialogShowingByTitlePredicate(String title) {
        this(title, StringComparators.strict());
    }

    public DialogShowingByTitlePredicate(String title, StringComparator comparator) {
        this.title = title;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return comp.isShowing() && comparator.equals(((Dialog) comp).getTitle(), title);
    }
}
