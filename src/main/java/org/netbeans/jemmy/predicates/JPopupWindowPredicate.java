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
import javax.swing.JPopupMenu;
import org.netbeans.jemmy.ComponentSearcher;

public final class JPopupWindowPredicate implements Predicate<Component> {
    final Predicate<Component> ppFinder;
    final Predicate<Component> subFinder;

    public JPopupWindowPredicate() {
        this(PredicatesJ.alwaysTrue());
    }

    public JPopupWindowPredicate(Predicate<Component> componentChooser) {
        subFinder = PredicatesJ.ofShowing(JPopupMenu.class, componentChooser);
        ppFinder = new ShowingAndVisibleSubPredicateCallingPredicate(subFinder);
    }

    @Override
    public boolean test(Component comp) {
        if (comp.isShowing() && (comp instanceof Window)) {
            ComponentSearcher cs = new ComponentSearcher((Container) comp);
            return cs.findComponent(ppFinder) != null;
        }

        return false;
    }
}
