/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.netbeans.jemmy.predicates;

import java.awt.Component;
import java.util.function.Predicate;
import javax.swing.JTable;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.util.StringComparator;

public final class JTableByCellValuePredicate implements Predicate<Component> {
    private final int column;
    private final StringComparator comparator;
    private final @Nullable String label;
    private final int row;

    public JTableByCellValuePredicate(@Nullable String label, int row, int column, StringComparator comparator) {
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
