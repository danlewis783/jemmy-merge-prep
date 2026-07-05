package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class TrimmingTooltipPredicate implements Predicate<Component> {
    private int currentIndex = 0;
    private final StringComparator comparator;
    private final int targetIndex;
    private final String tooltip;

    public TrimmingTooltipPredicate(String tooltip, StringComparator comparator, int index) {
        this.tooltip = tooltip;
        this.comparator = comparator;
        this.targetIndex = index;
    }

    @Override
    public boolean test(Component comp) {
        if (currentIndex++ < targetIndex) {
            return false;
        }

        if (comp instanceof JComponent) {
            JComponent jComponent = (JComponent) comp;
            String toolTipText = jComponent.getToolTipText();
            if (toolTipText != null) {
                return comparator.equals(toolTipText.trim(), tooltip);
            }
        }

        return false;
    }
}
