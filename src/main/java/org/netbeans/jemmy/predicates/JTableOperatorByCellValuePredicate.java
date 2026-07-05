package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JTableOperatorByCellValuePredicate implements Predicate<JTableOperator> {
    private final int column;
    private final StringComparator comparator;
    private final String expectedCellValue;
    private final int row;

    public JTableOperatorByCellValuePredicate(String expectedCellValue, int row, int column,
            StringComparator comparator) {
        this.expectedCellValue = expectedCellValue;
        this.row = row;
        this.column = column;
        this.comparator = comparator;
    }

    @Override
    public boolean test(JTableOperator jTableOp) {
        if (expectedCellValue == null) {
            return true;
        }

        if ((jTableOp.getRowCount() > row) && (jTableOp.getColumnCount() > column)) {
            int r = row;
            if (r == -1) {
                int[] rows = jTableOp.getSelectedRows();
                if (rows.length != 0) {
                    r = rows[0];
                } else {
                    return false;
                }
            }

            int c = column;
            if (c == -1) {
                int[] columns = jTableOp.getSelectedColumns();
                if (columns.length != 0) {
                    c = columns[0];
                } else {
                    return false;
                }
            }

            Object value = jTableOp.getValueAt(r, c);

            return (value != null) && comparator.equals(value.toString(), expectedCellValue);
        }

        return false;
    }
}
