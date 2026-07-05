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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JTextFieldOperatorTest {

    private JFrame frame;
    private JTextField textField;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                textField = new JTextField("JTextFieldOperatorTest");
                textField.setName("JTextFieldOperatorTest");
                frame.getContentPane().add(textField);
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
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1);
        assertNotNull(operator2);
        JTextFieldOperator operator3 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator3);
        JTextFieldOperator operator4 =
                new JTextFieldOperator(operator1, "JTextFieldOperatorTest", StringComparators.strict());
        assertNotNull(operator4);
    }

    @Test
    void testFindJTextField() {
        JTextField textField1 = JTextFieldOperator.findJTextField(frame, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(textField1);
        JTextField textField2 = JTextFieldOperator.findJTextField(
                frame, "JTextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textField2);
    }

    @Test
    void testWaitJTextField() {
        JTextField textField1 = JTextFieldOperator.waitJTextField(frame, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(textField1);
        JTextField textField2 = JTextFieldOperator.waitJTextField(
                frame, "JTextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(textField2);
    }

    @Test
    void testWaitText() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        operator2.waitText("JTextFieldOperatorTest", StringComparators.strict());
        assertEquals("JTextFieldOperatorTest", textField.getText());
        operator2.waitText("JTextFieldOperatorTest\n", StringComparators.strict());
        assertEquals("JTextFieldOperatorTest", textField.getText());
    }

    @Test
    void testAddActionListener() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        ActionListener listener = event -> {};
        operator2.addActionListener(listener);
        assertEquals(listener, textField.getActionListeners()[0]);
        operator2.removeActionListener(listener);
        assertEquals(0, textField.getActionListeners().length);
    }

    @Test
    void testGetColumns() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        operator2.setColumns(10);
        assertEquals(10, operator2.getColumns());
        assertEquals(textField.getColumns(), operator2.getColumns());
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        operator2.setHorizontalAlignment(SwingConstants.RIGHT);
        assertEquals(SwingConstants.RIGHT, operator2.getHorizontalAlignment());
        assertEquals(textField.getHorizontalAlignment(), operator2.getHorizontalAlignment());
    }

    @Test
    void testGetScrollOffset() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        operator2.setScrollOffset(operator2.getScrollOffset());
    }

    @Test
    void testPostActionEvent() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1, PredicatesJ.byName("JTextFieldOperatorTest"));
        assertNotNull(operator2);
        operator2.setActionCommand("ACTION_COMMAND");
        ActionListener1 listener = new ActionListener1();
        operator2.addActionListener(listener);
        operator2.postActionEvent();
        assertEquals(listener.actionCommand, "ACTION_COMMAND");
    }

    @Test
    void testGetHorizontalVisibility() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JTextFieldOperator operator2 = new JTextFieldOperator(operator1);
        assertNotNull(operator2);
        operator2.getHorizontalVisibility();
    }

    class ActionListener1 implements ActionListener {
        String actionCommand;

        @Override
        public void actionPerformed(ActionEvent event) {
            actionCommand = event.getActionCommand();
        }
    }
}
