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
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;
import org.netbeans.jemmy.util.StringComparators;

class jemmy_037 {

    @Test
    void test() {
        Application_037.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Application_037");
        JFrameOperator fro = new JFrameOperator(win);
        JTabbedPaneOperator tb = new JTabbedPaneOperator(fro);
        tb.selectPage("Swing", StringComparators.strict());
        JScrollBarOperator scroll0 = new JScrollBarOperator(fro);
        scroll0.scrollToMaximum();
        scroll0.scrollToMinimum();
        JScrollBarOperator scroll1 = new JScrollBarOperator(fro, 1);
        scroll1.scrollToMaximum();
        scroll1.scrollToMinimum();
        tb.selectPage("AWT", StringComparators.strict());
        ScrollbarOperator awscroll0 = new ScrollbarOperator(fro);
        awscroll0.scrollToMaximum();
        awscroll0.scrollToMinimum();
        ScrollbarOperator awscroll1 = new ScrollbarOperator(fro, 1);
        awscroll1.scrollToMaximum();
        awscroll1.scrollToMinimum();
    }
}
