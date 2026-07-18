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

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JRadioButtonOperatorTest {
    private JFrame frame;
    private JRadioButton radioButton;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            radioButton = new JRadioButton("JRadioButtonOperatorTest");
            radioButton.setName("JRadioButtonOperatorTest");
            frame.getContentPane().add(radioButton);
            frame.pack();
            frame.setLocationRelativeTo(null);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    private void showFrame() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> frame.setVisible(true));
    }

    @Test
    void testConstructor() throws InterruptedException, InvocationTargetException {
        showFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JRadioButtonOperator operator1 = JRadioButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        JRadioButtonOperator operator2 =
                JRadioButtonOperator.waitFor(operator, "JRadioButtonOperatorTest", StringComparators.strict());
        assertThat(operator2).isNotNull();
        JRadioButtonOperator operator3 =
                JRadioButtonOperator.waitFor(operator, ComponentPredicates.byName("JRadioButtonOperatorTest"));
        assertThat(operator3).isNotNull();
        JRadioButtonOperator operator4 = JRadioButtonOperator.of(radioButton);
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJRadioButton() throws InterruptedException, InvocationTargetException {
        showFrame();
        JRadioButton radioButton1 = JRadioButtonOperator.findJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(radioButton1).isNotNull();
        JRadioButton radioButton2 =
                JRadioButtonOperator.findJRadioButton(frame, ComponentPredicates.byName("JRadioButtonOperatorTest"));
        assertThat(radioButton2).isNotNull();
    }

    @Test
    void testWaitJRadioButton() throws InterruptedException, InvocationTargetException {
        showFrame();
        JRadioButton radioButton1 = JRadioButtonOperator.waitJRadioButton(
                frame, "JRadioButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(radioButton1).isNotNull();
        JRadioButton radioButton2 =
                JRadioButtonOperator.waitJRadioButton(frame, ComponentPredicates.byName("JRadioButtonOperatorTest"));
        assertThat(radioButton2).isNotNull();
    }
}
