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

import static org.assertj.core.api.Assertions.assertThat;

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
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JRadioButtonOperator operator1 = JRadioButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JRadioButtonOperator operator2 =
                JRadioButtonOperator.waitFor(operator, "JRadioButtonOperatorTest", StringComparators.strict());
        assertThat(operator2).isNotNull();
        JRadioButtonOperator operator3 =
                JRadioButtonOperator.waitFor(operator, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertThat(operator3).isNotNull();
        JRadioButtonOperator operator4 = JRadioButtonOperator.of(radioButton);
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(radioButton1).isNotNull();
        JRadioButton radioButton2 =
                JRadioButtonOperator.findJRadioButton(frame, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertThat(radioButton2).isNotNull();
    }

    @Test
    void testWaitJRadioButton() {
        frame.setVisible(true);
        JRadioButton radioButton1 = JRadioButtonOperator.waitJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(radioButton1).isNotNull();
        JRadioButton radioButton2 =
                JRadioButtonOperator.waitJRadioButton(frame, PredicatesJ.byName("JRadioButtonOperatorTest"));
        assertThat(radioButton2).isNotNull();
    }
}
