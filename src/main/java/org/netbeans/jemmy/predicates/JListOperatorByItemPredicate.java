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
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JListOperatorByItemPredicate<T extends JListOperator> implements Predicate<T> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String label;

    public JListOperatorByItemPredicate(String label, int itemIndex, StringComparator comparator) {
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(T jListOp) {
        if (label == null) {
            return true;
        }

        if ((jListOp).getModel().getSize() > itemIndex) {
            int ii = itemIndex;
            if (ii == -1) {
                ii = (jListOp).getSelectedIndex();

                if (ii == -1) {
                    return false;
                }
            }

            return comparator.equals((jListOp).getModel().getElementAt(ii).toString(), label);
        }

        return false;
    }
}
