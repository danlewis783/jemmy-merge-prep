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

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JRadioButtonOperatorTest {
    private JFrame frame;
    private JRadioButton radioButton;

    @BeforeEach
    void beforeEach() {
        frame = new JFrame();
        radioButton = new JRadioButton("JRadioButtonOperatorTest");
        radioButton.setName("JRadioButtonOperatorTest");
        frame.getContentPane().add(radioButton);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @AfterEach
    void afterEach() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JRadioButtonOperator operator1 = new JRadioButtonOperator(operator);
        assertNotNull(operator1);
        JRadioButtonOperator operator2 =
                new JRadioButtonOperator(operator, "JRadioButtonOperatorTest", StringComparators.strict());
        assertNotNull(operator2);
        JRadioButtonOperator operator3 =
                new JRadioButtonOperator(operator, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertNotNull(operator3);
        JRadioButtonOperator operator4 = new JRadioButtonOperator(radioButton);
        assertNotNull(operator4);
    }

    @Test
    void testFindJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(radioButton1);
        JRadioButton radioButton2 =
                JRadioButtonOperator.findJRadioButton(frame, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertNotNull(radioButton2);
    }

    @Test
    void testWaitJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.waitJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(radioButton1);
        JRadioButton radioButton2 =
                JRadioButtonOperator.waitJRadioButton(frame, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertNotNull(radioButton2);
    }
}
