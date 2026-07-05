
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import java.awt.*;

public final class DialogSubPredicate implements Predicate<Component> {
    private final Predicate<Component> predicate;

    public DialogSubPredicate(Predicate<Component> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof Dialog) && comp.isShowing() && comp.isVisible() && predicate.test(comp);
    }
}
