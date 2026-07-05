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
package org.netbeans.jemmy.operators;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.EventQueue;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JCheckBoxOperatorTest {

    private JCheckBox checkBox;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            checkBox = new JCheckBox("JCheckBoxOperatorTest");
            checkBox.setName("JCheckBoxOperatorTest");
            frame.getContentPane().add(checkBox);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JCheckBoxOperator operator2 = new JCheckBoxOperator(operator1);
        assertNotNull(operator2);
        JCheckBoxOperator operator3 = new JCheckBoxOperator(operator1, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(operator3);
        JCheckBoxOperator operator4 =
                new JCheckBoxOperator(operator1, "JCheckBoxOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJCheckBox() {
        JCheckBox checkBox1 = JCheckBoxOperator.findJCheckBox(frame, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(checkBox1);
        JCheckBox checkBox2 = JCheckBoxOperator.findJCheckBox(
                frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkBox2);
    }

    @Test
    void testWaitJCheckBox() {
        JCheckBox checkBox1 = JCheckBoxOperator.waitJCheckBox(frame, PredicatesJ.byName("JCheckBoxOperatorTest"));
        assertNotNull(checkBox1);
        JCheckBox checkBox2 = JCheckBoxOperator.waitJCheckBox(
                frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(checkBox2);
    }
}
