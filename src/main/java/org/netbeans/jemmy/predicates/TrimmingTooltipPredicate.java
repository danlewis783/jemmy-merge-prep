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
import javax.swing.JComponent;
import org.netbeans.jemmy.util.StringComparator;

public final class TrimmingTooltipPredicate implements Predicate<Component> {
    private int currentIndex = 0;
    private final StringComparator comparator;
    private final int targetIndex;
    private final String tooltip;

    public TrimmingTooltipPredicate(String tooltip, StringComparator comparator, int index) {
        this.tooltip = tooltip;
        this.comparator = comparator;
        this.targetIndex = index;
    }

    @Override
    public boolean test(Component comp) {
        if (currentIndex++ < targetIndex) {
            return false;
        }

        if (comp instanceof JComponent) {
            JComponent jComponent = (JComponent) comp;
            String toolTipText = jComponent.getToolTipText();
            if (toolTipText != null) {
                return comparator.equals(toolTipText.trim(), tooltip);
            }
        }

        return false;
    }
}
