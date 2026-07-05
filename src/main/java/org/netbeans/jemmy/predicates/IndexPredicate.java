
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import java.awt.*;

public final class IndexPredicate implements Predicate<Component> {
    private final Predicate<Component> predicate;
    private final int index;
    private int curIndex;

    public IndexPredicate(Predicate<Component> predicate, int index) {
        this.index = index;
        this.predicate = predicate;
    }

    @Override
    public boolean test(Component comp) {
        if (comp.isShowing() && comp.isVisible() && predicate.test(comp)) {
            if (curIndex == index) {
                return true;
            }

            curIndex++;
        }

        return false;
    }
}
