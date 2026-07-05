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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertNotNull(operator2);
        JButtonOperator operator3 = new JButtonOperator(operator1, PredicatesJ.byName("JButtonOperatorTest"));
        assertNotNull(operator3);
        JButtonOperator operator4 = new JButtonOperator(operator1, "JButtonOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void findJButton() {
        JButton button1 = JButtonOperator.findJButton(frame.get(), PredicatesJ.byName("JButtonOperatorTest"));
        assertNotNull(button1);
        JButton button2 = JButtonOperator.findJButton(
                frame.get(), "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(button2);
    }

    @Test
    void waitJButton() {
        JButton button1 = JButtonOperator.waitJButton(frame.get(), PredicatesJ.byName("JButtonOperatorTest"));
        assertNotNull(button1);
        JButton button2 = JButtonOperator.waitJButton(
                frame.get(), "JButtonOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(button2);
    }

    @Test
    void isDefaultCapable() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertNotNull(operator2);
        operator2.setDefaultCapable(true);
        assertTrue(button.get().isDefaultCapable());
        assertEquals(operator2.isDefaultCapable(), button.get().isDefaultCapable());
        operator2.setDefaultCapable(false);
        assertFalse(button.get().isDefaultCapable());
        assertEquals(operator2.isDefaultCapable(), button.get().isDefaultCapable());
    }

    @Test
    void prepareToClick() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertNotNull(operator2);
        operator2.prepareToClick();
        JButtonOperator operator3 = new JButtonOperator(operator1);
        assertNotNull(operator3);
        assertTrue(operator3.isVisible());
    }

    @Test
    void issue72187() throws Exception {
        button.get()
                .addActionListener(
                        event -> EventQueue.invokeLater(() -> button.get().setVisible(false)));
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JButtonOperator operator2 = new JButtonOperator(operator1);
        assertNotNull(operator2);
        operator2.press();
        operator2.release();
        operator2.waitState(new JComponentOperatorVisiblePredicate(false));
        assertFalse(button.get().isVisible());
        EventQueue.invokeAndWait(() -> button.get().setVisible(true));
        JButtonOperator operator3 = new JButtonOperator(operator1);
        assertNotNull(operator3);
        operator3.clickMouse();
        operator3.waitState(new JComponentOperatorVisiblePredicate(false));
        assertFalse(button.get().isVisible());
        EventQueue.invokeAndWait(() -> button.get().setVisible(true));
        JButtonOperator operator4 = new JButtonOperator(operator1);
        assertNotNull(operator4);
        operator4.push();
        assertFalse(operator4.isVisible());
        assertFalse(button.get().isVisible());
    }
}
