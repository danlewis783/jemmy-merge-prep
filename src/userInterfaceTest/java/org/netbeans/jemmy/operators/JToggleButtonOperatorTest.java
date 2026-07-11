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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JToggleButtonOperatorTest {

    private JFrame frame;
    private JToggleButton toggleButton;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            toggleButton = new JToggleButton("JToggleButtonOperatorTest");
            toggleButton.setName("JToggleButtonOperatorTest");
            frame.getContentPane().add(toggleButton);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JToggleButtonOperator operator2 = JToggleButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        JToggleButtonOperator operator3 =
                JToggleButtonOperator.waitFor(operator1, ComponentPredicates.byName("JToggleButtonOperatorTest"));
        assertThat(operator3).isNotNull();
        JToggleButtonOperator operator4 =
                JToggleButtonOperator.waitFor(operator1, StringComparators.strict(), "JToggleButtonOperatorTest");
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJToggleButton() {
        JToggleButton toggleButton1 = JToggleButtonOperator.findJToggleButton(
                frame, "JToggleButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(toggleButton1).isNotNull();
        JToggleButton toggleButton2 =
                JToggleButtonOperator.findJToggleButton(frame, ComponentPredicates.byName("JToggleButtonOperatorTest"));
        assertThat(toggleButton2).isNotNull();
    }

    @Test
    void testWaitJToggleButton() {
        JToggleButton toggleButton1 = JToggleButtonOperator.waitJToggleButton(
                frame, "JToggleButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(toggleButton1).isNotNull();
        JToggleButton toggleButton2 =
                JToggleButtonOperator.waitJToggleButton(frame, ComponentPredicates.byName("JToggleButtonOperatorTest"));
        assertThat(toggleButton2).isNotNull();
    }

    @Test
    void testPrepareToClick() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JToggleButtonOperator operator2 = JToggleButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        operator2.prepareToClick();
        assertThat(onQueue(toggleButton::isVisible)).isTrue();
    }
}
