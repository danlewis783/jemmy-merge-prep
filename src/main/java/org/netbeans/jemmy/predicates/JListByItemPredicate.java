package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JListByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String label;

    public JListByItemPredicate(String label, int itemIndex, StringComparator comparator) {
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JList) {
            if (label == null) {
                return true;
            }

            if (((JList) comp).getModel().getSize() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = ((JList) comp).getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                return comparator.equals(((JList) comp).getModel().getElementAt(ii).toString(), label);
            }
        }

        return false;
    }
}
