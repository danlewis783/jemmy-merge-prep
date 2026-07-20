package org.netbeans.jemmy.predicates;

import org.jetbrains.annotations.Nullable;
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

            if (table.getRowCount() > row && table.getColumnCount() > column) {
                int r = row;

                if (r == -1) {
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
        final Point point = tableOp.getPointToClick(r, c);
        final MouseEvent event = new MouseEvent(table, MouseEvent.MOUSE_ENTERED, 0, 0,
                (int) point.getX(), (int) point.getY(), 1, false);
        return stringComparator.equals(tableOp.getToolTipText(event), tooltip);
    }

    protected StringComparator getComparator() {
        return comparator;
    }
}