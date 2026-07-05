
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;

public final class JTreeByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;
    private final int rowIndex;

    public JTreeByItemPredicate(String label, int rowIndex, StringComparator comparator) {
        this.label = label;
        this.rowIndex = rowIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JTree) {
            if (label == null) {
                return true;
            }

            if (((JTree) comp).getRowCount() > rowIndex) {
                int ii = rowIndex;
                if (ii == -1) {
                    int[] rows = ((JTree) comp).getSelectionRows();
                    if ((rows != null) && (rows.length > 0)) {
                        ii = rows[0];
                    } else {
                        return false;
                    }
                }

                TreePath path = ((JTree) comp).getPathForRow(ii);
                if (path != null) {
                    return comparator.equals(path.getPathComponent(path.getPathCount() - 1).toString(), label);
                }
            }
        }

        return false;
    }
}
