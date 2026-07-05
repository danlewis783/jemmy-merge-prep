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
package org.netbeans.jemmy.testing;

import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JSplitPaneOperator;

class jemmy_019 {

    @Test
    void test() {
        Application_019.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        JFrame frm = JFrameOperator.waitJFrame("Application_019");
        JSplitPaneOperator split = new JSplitPaneOperator(JSplitPaneOperator.findJSplitPane(frm));
        split.moveDivider(1d);
        split.moveDivider(0d);
        split.moveDivider(0.5);
        split.expandRight();
        split.expandLeft();
        split.expandLeft();
        split.expandRight();
        split.moveToMinimum();
        split.moveToMaximum();
    }
}
