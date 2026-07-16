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
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.util.StringComparator;

public final class JTreeByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final @Nullable String label;
    private final int rowIndex;

    public JTreeByItemPredicate(@Nullable String label, int rowIndex, StringComparator comparator) {
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
                    return comparator.equals(
                            path.getPathComponent(path.getPathCount() - 1).toString(), label);
                }
            }
        }

        return false;
    }
}
