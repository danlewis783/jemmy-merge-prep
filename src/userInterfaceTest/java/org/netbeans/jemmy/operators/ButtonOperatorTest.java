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

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class ButtonOperatorTest {

    private Button button;
    private Frame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            button = new Button("ButtonOperatorTest");
            button.setName("ButtonOperatorTest");
            frame.add(button);
            // AWT operators use real Robot clicks at screen coordinates; if another window
            // (e.g. a lingering frame from an adjacent test JVM) overlaps this frame, the
            // click lands on that window instead and the test times out waiting on component state
            frame.setAlwaysOnTop(true);
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
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        ButtonOperator operator2 = ButtonOperator.waitFor(operator, ComponentPredicates.byName("ButtonOperatorTest"));
        assertThat(operator2).isNotNull();
        ButtonOperator operator3 = ButtonOperator.waitFor(operator, "ButtonOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindButton() {
        Button button1 =
                ButtonOperator.findButton(frame, "ButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button1).isNotNull();
        Button button2 = ButtonOperator.findButton(frame, ComponentPredicates.byName("ButtonOperatorTest"));
        assertThat(button2).isNotNull();
    }

    @Test
    void testWaitButton() {
        Button button1 =
                ButtonOperator.waitButton(frame, "ButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button1).isNotNull();
        Button button2 = ButtonOperator.waitButton(frame, ComponentPredicates.byName("ButtonOperatorTest"));
        assertThat(button2).isNotNull();
    }

    @Test
    void testGetActionCommand() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setActionCommand("TEST");
        assertThat(operator1.getActionCommand()).isEqualTo("TEST");
    }

    @Test
    void testGetLabel() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.setLabel("TEST");
        assertThat(operator1.getLabel()).isEqualTo("TEST");
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.addActionListener(event -> {});
        assertThat(onQueue(button::getActionListeners)).hasSize(1);
        ActionListener[] listeners = onQueue(button::getActionListeners);
        operator1.removeActionListener(listeners[0]);
        assertThat(onQueue(button::getActionListeners)).isEmpty();
    }

    @Test
    void testPush() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.push();
    }

    @Test
    void testPushNoBlock() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.pushNoBlock();
        operator1.push();
    }

    @Test
    void testRelease() {
        FrameOperator operator = FrameOperator.waitFor();
        assertThat(operator).isNotNull();
        ButtonOperator operator1 = ButtonOperator.waitFor(operator);
        assertThat(operator1).isNotNull();
        operator1.press();
        operator1.release();
    }
}
