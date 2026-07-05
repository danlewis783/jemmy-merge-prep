
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import javax.swing.*;
import java.awt.*;

public final class TooltipIsVisibleAndShowingPredicate implements Predicate<Component> {
    @Override
    public boolean test(Component comp) {
        return comp instanceof JToolTip && comp.isShowing() && comp.isVisible();
    }
}
