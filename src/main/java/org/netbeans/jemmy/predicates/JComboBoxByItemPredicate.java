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
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.util.StringComparator;

public final class JComboBoxByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final @Nullable String label;

    /**
     * @param label if null, the first combo box encountered will be accepted
     * @param itemIndex the index of the combo box item to check, or -1 to check the currently selected item
     */
    public JComboBoxByItemPredicate(@Nullable String label, int itemIndex, StringComparator comparator) {
        if (itemIndex < -1) {
            throw new IllegalArgumentException("invalid itemIndex");
        }
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof JComboBox) {
            if (label == null) {
                return true;
            }

            JComboBox<?> jComboBox = (JComboBox<?>) comp;
            ComboBoxModel<?> model = jComboBox.getModel();
            if (model.getSize() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = jComboBox.getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                Object item = model.getElementAt(ii);
                return comparator.equals(String.valueOf(item), label);
            }
        }

        return false;
    }
}
