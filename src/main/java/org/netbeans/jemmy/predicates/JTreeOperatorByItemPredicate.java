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

import java.util.function.Predicate;
import javax.swing.tree.TreePath;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JTreeOperatorByItemPredicate implements Predicate<JTreeOperator> {
    private final StringComparator comparator;
    private final String expectedPathComponentToString;
    private final int rowIndex;

    public JTreeOperatorByItemPredicate(
            String expectedPathComponentToString, int rowIndex, StringComparator comparator) {
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
                return comparator.equals(
                        path.getPathComponent(path.getPathCount() - 1).toString(), expectedPathComponentToString);
            }
        }

        return false;
    }
}
