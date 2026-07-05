package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.tree.TreePath;

public final class JTreeOperatorByItemPredicate implements Predicate<JTreeOperator> {
    private final StringComparator comparator;
    private final String expectedPathComponentToString;
    private final int rowIndex;

    public JTreeOperatorByItemPredicate(String expectedPathComponentToString, int rowIndex, StringComparator comparator) {
        this.expectedPathComponentToString = expectedPathComponentToString;
        this.rowIndex = rowIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(JTreeOperator jTreeOperator) {
        if (expectedPathComponentToString == null) {
            return true;
        }

        if (jTreeOperator.getRowCount() > rowIndex) {
            int ii = rowIndex;
            if (ii == -1) {
                int[] rows = jTreeOperator.getSelectionRows();
                if ((rows != null) && (rows.length > 0)) {
                    ii = rows[0];
                } else {
                    return false;
                }
            }

            TreePath path = jTreeOperator.getPathForRow(ii);
            if (path != null) {
                return comparator.equals(path.getPathComponent(path.getPathCount() - 1).toString(), expectedPathComponentToString);
            }
        }

        return false;
    }
}
