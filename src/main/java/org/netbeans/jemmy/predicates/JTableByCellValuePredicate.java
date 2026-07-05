
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JTableByCellValuePredicate implements Predicate<Component> {
    private final int column;
    private final StringComparator comparator;
    private final String label;
    private final int row;

    public JTableByCellValuePredicate(String label, int row, int column, StringComparator comparator) {
        this.label = label;
        this.row = row;
        this.column = column;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JTable) {
            if (label == null) {
                return true;
            }

            if (((JTable) comp).getRowCount() > row && ((JTable) comp).getColumnCount() > column) {
                int r = row;
                if (r == -1) {
                    int[] rows = ((JTable) comp).getSelectedRows();
                    if (rows.length != 0) {
                        r = rows[0];
                    } else {
                        return false;
                    }
                }

                int c = column;
                if (c == -1) {
                    int[] columns = ((JTable) comp).getSelectedColumns();
                    if (columns.length != 0) {
                        c = columns[0];
                    } else {
                        return false;
                    }
                }

                Object value = ((JTable) comp).getValueAt(r, c);
                return (value != null) && comparator.equals(value.toString(), label);
            }
        }

        return false;
    }
}
