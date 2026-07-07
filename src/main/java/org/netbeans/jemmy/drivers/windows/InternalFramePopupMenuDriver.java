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

package org.netbeans.jemmy.drivers.windows;

import java.util.Objects;
import javax.swing.UIManager;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.util.StringComparators;

/**
 * Internal frame driver for look and feels whose title actions (close, minimize, maximize, restore) live in a popup
 * menu behind a single title button (Motif-style) rather than in title-pane buttons. Ported from openjdk/jemmy-v2
 * (CODETOOLS-7902300).
 */
public class InternalFramePopupMenuDriver extends DefaultInternalFrameDriver {

    @Override
    public void requestClose(ComponentOperator oper) {
        checkSupported(oper);
        pushMenuItem(oper, titlePaneActionText("InternalFrameTitlePane.closeButtonText"));
    }

    @Override
    public void iconify(ComponentOperator oper) {
        checkSupported(oper);
        pushMenuItem(oper, titlePaneActionText("InternalFrameTitlePane.minimizeButtonText"));
    }

    @Override
    public void maximize(ComponentOperator oper) {
        checkSupported(oper);

        if (!((JInternalFrameOperator) oper).isMaximum()) {
            if (!((JInternalFrameOperator) oper).isSelected()) {
                activate(oper);
            }

            pushMenuItem(oper, titlePaneActionText("InternalFrameTitlePane.maximizeButtonText"));
        }
    }

    @Override
    public void demaximize(ComponentOperator oper) {
        checkSupported(oper);

        if (((JInternalFrameOperator) oper).isMaximum()) {
            if (!((JInternalFrameOperator) oper).isSelected()) {
                activate(oper);
            }

            pushMenuItem(oper, titlePaneActionText("InternalFrameTitlePane.restoreButtonText"));
        }
    }

    private static String titlePaneActionText(String key) {
        return Objects.requireNonNull(UIManager.getString(key), key + " UI default");
    }

    private static void pushMenuItem(ComponentOperator oper, String menuText) {
        ((JInternalFrameOperator) oper).getPopupButton().push();
        JPopupMenuOperator popupMenu = new JPopupMenuOperator();
        new JMenuItemOperator(popupMenu, menuText, StringComparators.strict()).push();
    }
}
