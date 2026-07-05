
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JCheckBoxMenuItemByLabelPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public JCheckBoxMenuItemByLabelPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JCheckBoxMenuItem) {
            if (((JCheckBoxMenuItem) comp).getText() != null) {
                return comparator.equals(((JCheckBoxMenuItem) comp).getText(), label);
            }
        }

        return false;
    }
}
