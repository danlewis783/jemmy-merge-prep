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
import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JComponentOperatorVisiblePredicate;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class JButtonOperatorTest {

    private JButton button;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            button = new JButton("JButtonOperatorTest");
            button.setName("JButtonOperatorTest");
            frame.getContentPane().add(button);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void constructor() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        JButtonOperator operator3 =
                JButtonOperator.waitFor(operator1, ComponentPredicates.byName("JButtonOperatorTest"));
        assertThat(operator3).isNotNull();
        JButtonOperator operator4 =
                JButtonOperator.waitFor(operator1, "JButtonOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void findJButton() {
        JButton button1 = JButtonOperator.findJButton(frame, ComponentPredicates.byName("JButtonOperatorTest"));
        assertThat(button1).isNotNull();
        JButton button2 =
                JButtonOperator.findJButton(frame, "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void waitJButton() {
        JButton button1 = JButtonOperator.waitJButton(frame, ComponentPredicates.byName("JButtonOperatorTest"));
        assertThat(button1).isNotNull();
        JButton button2 =
                JButtonOperator.waitJButton(frame, "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void isDefaultCapable() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        operator2.setDefaultCapable(true);
        assertThat(onQueue(button::isDefaultCapable)).isTrue();
        assertThat(onQueue(button::isDefaultCapable)).isEqualTo(operator2.isDefaultCapable());
        operator2.setDefaultCapable(false);
        assertThat(onQueue(button::isDefaultCapable)).isFalse();
        assertThat(onQueue(button::isDefaultCapable)).isEqualTo(operator2.isDefaultCapable());
    }

    @Test
    void prepareToClick() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        operator2.prepareToClick();
        JButtonOperator operator3 = JButtonOperator.waitFor(operator1);
        assertThat(operator3).isNotNull();
        assertThat(operator3.isVisible()).isTrue();
    }

    @Test
    void issue72187() throws InterruptedException, InvocationTargetException {
        button.addActionListener(event -> EventQueue.invokeLater(() -> button.setVisible(false)));
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        operator2.press();
        operator2.release();
        operator2.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(onQueue(button::isVisible)).isFalse();
        EventQueue.invokeAndWait(() -> button.setVisible(true));
        JButtonOperator operator3 = JButtonOperator.waitFor(operator1);
        assertThat(operator3).isNotNull();
        operator3.clickMouse();
        operator3.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(onQueue(button::isVisible)).isFalse();
        EventQueue.invokeAndWait(() -> button.setVisible(true));
        JButtonOperator operator4 = JButtonOperator.waitFor(operator1);
        assertThat(operator4).isNotNull();
        operator4.push();
        assertThat(operator4.isVisible()).isFalse();
        assertThat(onQueue(button::isVisible)).isFalse();
    }
}
