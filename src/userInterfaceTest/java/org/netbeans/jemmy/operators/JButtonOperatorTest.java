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
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.JComponentOperatorVisiblePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JButtonOperatorTest {

    private final AtomicReference<JButton> button = new AtomicReference<>();
    private final AtomicReference<JFrame> frame = new AtomicReference<>();

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.set(new JFrame());
            button.set(new JButton("JButtonOperatorTest"));
            button.get().setName("JButtonOperatorTest");
            frame.get().getContentPane().add(button.get());
            frame.get().pack();
            frame.get().setLocationRelativeTo(null);
            frame.get().setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.get().setVisible(false);
            frame.get().dispose();
            frame.set(null);
        });
    }

    @Test
    void constructor() {
        JFrameOperator operator1 = new JFrameOperator();
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertThat(operator2).isNotNull();
        JButtonOperator operator3 = new JButtonOperator(operator1, PredicatesJ.byName("JButtonOperatorTest"));
        assertThat(operator3).isNotNull();
        JButtonOperator operator4 = new JButtonOperator(operator1, "JButtonOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void findJButton() {
        JButton button1 = JButtonOperator.findJButton(frame.get(), PredicatesJ.byName("JButtonOperatorTest"));
        assertThat(button1).isNotNull();
        JButton button2 = JButtonOperator.findJButton(
                frame.get(), "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void waitJButton() {
        JButton button1 = JButtonOperator.waitJButton(frame.get(), PredicatesJ.byName("JButtonOperatorTest"));
        assertThat(button1).isNotNull();
        JButton button2 = JButtonOperator.waitJButton(
                frame.get(), "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(button2).isNotNull();
    }

    @Test
    void isDefaultCapable() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertThat(operator2).isNotNull();
        operator2.setDefaultCapable(true);
        assertThat(button.get().isDefaultCapable()).isTrue();
        assertThat(button.get().isDefaultCapable()).isEqualTo(operator2.isDefaultCapable());
        operator2.setDefaultCapable(false);
        assertThat(button.get().isDefaultCapable()).isFalse();
        assertThat(button.get().isDefaultCapable()).isEqualTo(operator2.isDefaultCapable());
    }

    @Test
    void prepareToClick() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertThat(operator2).isNotNull();
        operator2.prepareToClick();
        JButtonOperator operator3 = new JButtonOperator(operator1);
        assertThat(operator3).isNotNull();
        assertThat(operator3.isVisible()).isTrue();
    }

    @Test
    void issue72187() throws Exception {
        button.get()
                .addActionListener(
                        event -> EventQueue.invokeLater(() -> button.get().setVisible(false)));
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertThat(operator2).isNotNull();
        operator2.press();
        operator2.release();
        operator2.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(button.get().isVisible()).isFalse();
        EventQueue.invokeAndWait(() -> button.get().setVisible(true));
        JButtonOperator operator3 = new JButtonOperator(operator1);
        assertThat(operator3).isNotNull();
        operator3.clickMouse();
        operator3.waitState(new JComponentOperatorVisiblePredicate(false));
        assertThat(button.get().isVisible()).isFalse();
        EventQueue.invokeAndWait(() -> button.get().setVisible(true));
        JButtonOperator operator4 = new JButtonOperator(operator1);
        assertThat(operator4).isNotNull();
        operator4.push();
        assertThat(operator4.isVisible()).isFalse();
        assertThat(button.get().isVisible()).isFalse();
    }
}
