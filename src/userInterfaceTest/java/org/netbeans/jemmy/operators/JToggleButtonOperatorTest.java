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
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
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
            TestWindows.place(frame);
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
        JToggleButtonOperator.waitFor(operator1);
        JToggleButtonOperator.waitFor(operator1, ComponentPredicates.byName("JToggleButtonOperatorTest"));
        JToggleButtonOperator.waitFor(operator1, StringComparators.strict(), "JToggleButtonOperatorTest");
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
        JToggleButtonOperator.waitJToggleButton(
                frame, "JToggleButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        JToggleButtonOperator.waitJToggleButton(frame, ComponentPredicates.byName("JToggleButtonOperatorTest"));
    }

    @Test
    void testPrepareToClick() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JToggleButtonOperator operator2 = JToggleButtonOperator.waitFor(operator1);
        operator2.prepareToClick();
        assertThat(onQueue(toggleButton::isVisible)).isTrue();
    }
}
