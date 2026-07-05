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

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

class jemmy_009 {

    @Test
    void test() {
        Application_009.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        JFrame frm0 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring());
        assertEquals(0, ((Application_009) frm0).getIndex());
        JFrame frm1 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring(), 1);
        assertEquals(1, ((Application_009) frm1).getIndex());
        JFrame frm2 = JFrameOperator.waitJFrame("Application_009", StringComparators.substring(), 2);
        assertEquals(2, ((Application_009) frm2).getIndex());
        JFrameOperator frm2o = new JFrameOperator(frm2);
        frm2o.setTitle("New Title");
        frm2o.waitTitle("New Title", StringComparators.strict());
    }
}
