
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JComponentByToolTipPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String tooltip;

    public JComponentByToolTipPredicate(String tooltip, StringComparator comparator) {
        this.tooltip = tooltip;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof JComponent) && comparator.equals(((JComponent) comp).getToolTipText(), tooltip);
    }
}
