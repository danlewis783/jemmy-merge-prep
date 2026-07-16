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
import javax.swing.JList;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.util.StringComparator;

public final class JListByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final @Nullable String label;

    public JListByItemPredicate(@Nullable String label, int itemIndex, StringComparator comparator) {
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JList) {
            if (label == null) {
                return true;
            }

            if (((JList<?>) comp).getModel().getSize() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = ((JList<?>) comp).getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                return comparator.equals(
                        ((JList<?>) comp).getModel().getElementAt(ii).toString(), label);
            }
        }

        return false;
    }
}
