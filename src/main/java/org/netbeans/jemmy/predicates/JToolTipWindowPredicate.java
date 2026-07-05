
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import java.awt.*;

public final class JToolTipWindowPredicate implements Predicate<Component> {
    private final Predicate<Component> componentChooser;

    public JToolTipWindowPredicate() {
        componentChooser = new TooltipIsVisibleAndShowingPredicate();
    }

    @Override
    public boolean test(Component comp) {
        return comp instanceof Window && comp.isShowing()
                && new ComponentSearcher((Container) comp).findComponent(componentChooser) != null;
    }
}
