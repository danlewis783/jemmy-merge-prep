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
import javax.swing.JTextField;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.DefaultVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_028
class VisualizerScrollTest {

    @Test
    void test() {
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).switchTab(true);
        ((DefaultVisualizer) ComponentOperator.getDefaultComponentVisualizer()).scroll(true);
        ObscuredFieldApp.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("Right one");
        JTextField trg = ((ObscuredFieldApp) win).getTarget();
        JTextFieldOperator trgo = new JTextFieldOperator(trg);
        trgo.clearText();
        trgo.typeText("Text supposed to be typed");
        JTextFieldOperator.waitJTextField(win, "Text supposed to be typed", StringComparators.strict());
    }
}
