
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JTabbedPaneByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String title;

    public JTabbedPaneByItemPredicate(String title, int itemIndex, StringComparator comparator) {
        this.title = title;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JTabbedPane) {
            if (title == null) {
                return true;
            }

            JTabbedPaneOperator tpo = new JTabbedPaneOperator((JTabbedPane) comp);
            if (tpo.getTabCount() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = tpo.getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                return comparator.equals(tpo.getTitleAt(ii), title);
            }
        }

        return false;
    }
}
