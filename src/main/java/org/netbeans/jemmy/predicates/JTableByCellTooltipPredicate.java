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

import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.JTable;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.function.Predicate;

public class JTableByCellTooltipPredicate implements Predicate<Component> {
    private final @Nullable String tooltip;
    private final int row;
    private final int column;
    private final StringComparator comparator = StringComparators.strict();

    public JTableByCellTooltipPredicate(@Nullable String tooltip, int row, int column) {
        this.tooltip = tooltip;
        if (row < -1) {
            throw new IllegalArgumentException("row must be zero, positive, or -1");
        }
        if (column < -1) {
            throw new IllegalArgumentException("column must be zero, positive, or -1");
        }
        this.row = row;
        this.column = column;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JTable) {
            final JTable table = (JTable) comp;

            if ((tooltip == null || table.getRowCount() > row)
                    && table.getColumnCount() > column) {
                int r = row;

                if (tooltip != null && r == -1) {
                    int[] rows = table.getSelectedRows();

                    if (rows.length != 0) {
                        r = rows[0];
                    } else {
                        return false;
                    }
                }

                int c = column;

                if (c == -1) {
                    int[] columns = table.getSelectedColumns();

                    if (columns.length != 0) {
                        c = columns[0];
                    } else {
                        return false;
                    }
                }

                return isMatchingTable(table, r, c, tooltip);
            }
        }

        return false;
    }

    public boolean isMatchingTable(final JTable table, int r, int c, String tooltip) {
        return isMatchingTable(JTableOperator.of(table), r, c, tooltip, comparator);
    }

    public static boolean isMatchingTable(final JTableOperator tableOp, int r, int c, String tooltip, StringComparator stringComparator) {
        if (tooltip == null) {
            return true;
        }

        Component source = tableOp.getSource();
        JTable table = (JTable) source;
        return QueueTool.getInstance().callOnQueue(() -> {
            Point point = tableOp.getPointToClick(r, c);
            MouseEvent event = new MouseEvent(table, MouseEvent.MOUSE_ENTERED, 0, 0,
                    (int) point.getX(), (int) point.getY(), 1, false);
            return stringComparator.equals(tableOp.getToolTipText(event), tooltip);
        });
    }

    protected StringComparator getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + fieldsToString() + "}";
    }

    protected String fieldsToString() {
        return "tooltip=" + (tooltip == null ? "null" : "\"" + tooltip + "\"") + ", row=" + row + ", column="
                + column;
    }
}
