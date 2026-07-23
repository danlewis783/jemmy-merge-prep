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
import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JComponentOperatorVisiblePredicate;
import org.netbeans.jemmy.testing.JemmyStateResetExtension;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=1, unit=TimeUnit.SECONDS)
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
            TestWindows.place(frame);
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
        JButtonOperator.waitFor(operator1);
        JButtonOperator.waitFor(operator1, PredicatesJ.byName("JButtonOperatorTest"));
        JButtonOperator.waitFor(operator1, "JButtonOperatorTest", StringComparators.strict());
    }

    @Test
    void findJButton() {
        JButton button1 = JButtonOperator.findJButton(frame, PredicatesJ.byName("JButtonOperatorTest"));
        assertThat(button1).isNotNull();
        JButton button2 =
                JButtonOperator.findJButton(frame, "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void waitJButton() {
        JButtonOperator.waitJButton(frame, PredicatesJ.byName("JButtonOperatorTest"));
        JButtonOperator.waitJButton(frame, "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void isDefaultCapable() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
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
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        operator2.prepareToClick();
        JButtonOperator operator3 = JButtonOperator.waitFor(operator1);
        assertThat(operator3.isVisible()).isTrue();
    }

    @Test
    void issue72187() throws InterruptedException, InvocationTargetException {
        button.addActionListener(event -> EventQueue.invokeLater(() -> button.setVisible(false)));
        JFrameOperator operator1 = JFrameOperator.waitFor();
        JButtonOperator operator2 = JButtonOperator.waitFor(operator1);
        operator2.press();
        operator2.release();
        operator2.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(onQueue(button::isVisible)).isFalse();
        EventQueue.invokeAndWait(() -> button.setVisible(true));
        JButtonOperator operator3 = JButtonOperator.waitFor(operator1);
        operator3.clickMouse();
        operator3.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(onQueue(button::isVisible)).isFalse();
        EventQueue.invokeAndWait(() -> button.setVisible(true));
        JButtonOperator operator4 = JButtonOperator.waitFor(operator1);
        operator4.push();
        assertThat(operator4.isVisible()).isFalse();
        assertThat(onQueue(button::isVisible)).isFalse();
    }
}
