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
import java.awt.Container;
import java.awt.Window;
import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

public final class JToolTipWindowPredicate implements Predicate<Component> {
    private final Predicate<Component> componentChooser;

    public JToolTipWindowPredicate() {
        componentChooser = new TooltipIsVisibleAndShowingPredicate();
    }

    @Override
    public boolean test(Component comp) {
        return comp instanceof Window
                && comp.isShowing()
                && new ComponentSearcher((Container) comp).findComponent(componentChooser) != null;
    }
}
