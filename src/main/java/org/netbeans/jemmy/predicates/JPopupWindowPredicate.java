
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import javax.swing.*;
import java.awt.*;

public final class JPopupWindowPredicate implements Predicate<Component> {
    final Predicate<Component> ppFinder;
    final Predicate<Component> subFinder;

    public JPopupWindowPredicate() {
        this(PredicatesJ.alwaysTrue());
    }

    public JPopupWindowPredicate(Predicate<Component> componentChooser) {
        subFinder = PredicatesJ.ofShowing(JPopupMenu.class, componentChooser);
        ppFinder = new ShowingAndVisibleSubPredicateCallingPredicate(subFinder);
    }

    @Override
    public boolean test(Component comp) {
        if (comp.isShowing() && (comp instanceof Window)) {
            ComponentSearcher cs = new ComponentSearcher((Container) comp);
            return cs.findComponent(ppFinder) != null;
        }

        return false;
    }

}
