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
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTextAreaOperator;
import org.netbeans.jemmy.util.StringComparators;

class jemmy_020 {

    @Test
    void test() {
        Application_020.main(new String[] {});
        String allChars =
                " !\"#$%&'()*,-./0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
        JFrame frm = JFrameOperator.waitJFrame("Application_020");
        JTextAreaOperator to = new JTextAreaOperator(
                JTextAreaOperator.findJTextArea(frm, null, StringComparators.caseInsensitiveSubstring()));
        to.typeText(allChars);
        assertEquals(allChars, to.getText());
        to.setText("");
        assertEquals("", to.getText());
        to.setText(allChars);
        assertEquals(allChars, to.getText());
        to.clearText();
    }
}
