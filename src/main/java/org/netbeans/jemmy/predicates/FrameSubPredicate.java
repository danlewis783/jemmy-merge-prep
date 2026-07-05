
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import java.awt.*;

public final class FrameSubPredicate implements Predicate<Component> {
    private final Predicate<Component> chooser;

    public FrameSubPredicate(Predicate<Component> chooser) {
        this.chooser = chooser;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof Frame) && comp.isShowing() && comp.isVisible() && chooser.test(comp);
    }
}
