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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.LabelUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JLabelOperatorTest {

    private JFrame frame;
    private JLabel label;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                label = new JLabel("JLabelOperatorTest");
                label.setName("JLabelOperatorTest");
                frame.getContentPane().add(label);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        JLabelOperator operator3 = new JLabelOperator(operator1, PredicatesJ.byName("JLabelOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void testFindJLabel() {
        JLabel label1 = JLabelOperator.findJLabel(frame, PredicatesJ.byName("JLabelOperatorTest"));
        assertNotNull(label1);
        JLabel label2 =
                JLabelOperator.findJLabel(frame, "JLabelOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(label2);
    }

    @Test
    void testWaitJLabel() {
        JLabel label1 = JLabelOperator.waitJLabel(frame, PredicatesJ.byName("JLabelOperatorTest"));
        assertNotNull(label1);
        JLabel label2 =
                JLabelOperator.waitJLabel(frame, "JLabelOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(label2);
    }

    @Test
    void testWaitText() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setText("JLabelOperatorTest-");
        assertEquals(operator2.getText(), "JLabelOperatorTest-");
        operator2.setText("JLabelOperatorTest");
        operator2.waitText("JLabelOperatorTest", StringComparators.strict());
        assertNotNull(operator2.getText());
    }

    @Test
    void testGetDisabledIcon() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setDisabledIcon(null);
        assertNull(operator2.getDisabledIcon());
    }

    @Test
    void testGetDisplayedMnemonic() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setDisplayedMnemonic('A');
        assertEquals('A', operator2.getDisplayedMnemonic());
        assertEquals(label.getDisplayedMnemonic(), operator2.getDisplayedMnemonic());
        operator2.setDisplayedMnemonic((int) 'A');
        assertEquals('A', operator2.getDisplayedMnemonic());
        assertEquals(label.getDisplayedMnemonic(), operator2.getDisplayedMnemonic());
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setHorizontalAlignment(SwingConstants.TRAILING);
        assertEquals(SwingConstants.TRAILING, operator2.getHorizontalAlignment());
    }

    @Test
    void testGetHorizontalTextPosition() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setHorizontalTextPosition(SwingConstants.LEFT);
        assertEquals(SwingConstants.LEFT, operator2.getHorizontalTextPosition());
    }

    @Test
    void testGetIcon() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setIcon(null);
        assertNull(operator2.getIcon());
    }

    @Test
    void testGetIconTextGap() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setIconTextGap(15);
        assertEquals(15, operator2.getIconTextGap());
    }

    @Test
    void testGetLabelFor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setLabelFor(frame);
        assertEquals(operator2.getLabelFor(), frame);
        assertEquals(operator2.getLabelFor(), label.getLabelFor());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        JLabelOperatorTestUI ui = new JLabelOperatorTestUI();
        operator2.setUI(ui);
        assertEquals(operator2.getUI(), ui);
        assertEquals(label.getUI(), ui);
    }

    @Test
    void testGetVerticalAlignment() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setVerticalAlignment(SwingConstants.TOP);
        assertEquals(SwingConstants.TOP, operator2.getVerticalAlignment());
    }

    @Test
    void testGetVerticalTextPosition() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JLabelOperator operator2 = new JLabelOperator(operator1);
        assertNotNull(operator2);
        operator2.setVerticalTextPosition(SwingConstants.TOP);
        assertEquals(SwingConstants.TOP, operator2.getVerticalTextPosition());
    }

    private class JLabelOperatorTestUI extends LabelUI {}
}
