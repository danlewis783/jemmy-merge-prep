
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class FrameShowingByTitlePredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String title;

    public FrameShowingByTitlePredicate(String title, StringComparator comparator) {
        this.title = title;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return comp instanceof Frame && comp.isShowing() && comparator.equals(((Frame) comp).getTitle(), title);

    }
}
