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
package org.netbeans.jemmy.functions;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JList;
import javax.swing.plaf.basic.ComboPopup;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.predicates.PopupWindowPredicate;

public class ComboBoxPopupListFunction implements Function<Void, Component> {
    private final JComboBoxOperator jComboBoxOperator;
    private final Predicate<Component> popupWindowChooser;
    private final Predicate<Component> predicate;

    public ComboBoxPopupListFunction(JComboBoxOperator jComboBoxOperator) {
        this.jComboBoxOperator = jComboBoxOperator;
        predicate = new JListInsideComboPopupPredicate();
        popupWindowChooser = new PopupWindowPredicate(predicate);
    }

    @Override
    public @Nullable Component apply(Void obj) {
        Window popupWindow;
        if (popupWindowChooser.test(jComboBoxOperator.getWindow())) {
            popupWindow = jComboBoxOperator.getWindow();
        } else {
            popupWindow = WindowFunction.getWindow(jComboBoxOperator.getWindow(), popupWindowChooser, 0);
        }

        if (popupWindow != null) {
            ComponentSearcher componentSearcher = new ComponentSearcher(popupWindow);

            return componentSearcher.findComponent(predicate);
        } else {
            return null;
        }
    }

    private static class JListInsideComboPopupPredicate implements Predicate<Component> {
        @Override
        public boolean test(Component comp) {
            if (comp instanceof JList) {
                Container cont = (Container) comp;
                while ((cont = cont.getParent()) != null) {
                    if (cont instanceof ComboPopup) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
