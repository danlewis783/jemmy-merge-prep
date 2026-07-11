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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JTextFieldOperatorTest {

    private JFrame frame;
    private JTextField textField;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            textField = new JTextField("JTextFieldOperatorTest");
            textField.setName("JTextFieldOperatorTest");
            frame.getContentPane().add(textField);
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
        JTextFieldOperator operator2 = JTextFieldOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        JTextFieldOperator operator3 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator3).isNotNull();
        JTextFieldOperator operator4 =
                JTextFieldOperator.waitFor(operator1, "JTextFieldOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJTextField() {
        JTextField textField1 =
                JTextFieldOperator.findJTextField(frame, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(textField1).isNotNull();
        JTextField textField2 = JTextFieldOperator.findJTextField(
                frame, "JTextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textField2).isNotNull();
    }

    @Test
    void testWaitJTextField() {
        JTextField textField1 =
                JTextFieldOperator.waitJTextField(frame, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(textField1).isNotNull();
        JTextField textField2 = JTextFieldOperator.waitJTextField(
                frame, "JTextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textField2).isNotNull();
    }

    @Test
    void testWaitText() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.waitText("JTextFieldOperatorTest", StringComparators.strict());
        assertThat(onQueue(() -> textField.getText())).isEqualTo("JTextFieldOperatorTest");
        operator2.waitText("JTextFieldOperatorTest\n", StringComparators.strict());
        assertThat(onQueue(() -> textField.getText())).isEqualTo("JTextFieldOperatorTest");
    }

    @Test
    void testAddActionListener() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        ActionListener listener = event -> {};
        operator2.addActionListener(listener);
        ActionListener[] listeners = onQueue(textField::getActionListeners);
        assertThat(listeners[0]).isEqualTo(listener);
        operator2.removeActionListener(listener);
        assertThat(onQueue(textField::getActionListeners)).isEmpty();
    }

    @Test
    void testGetColumns() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.setColumns(10);
        assertThat(operator2.getColumns()).isEqualTo(10);
        assertThat(operator2.getColumns()).isEqualTo(onQueue(textField::getColumns));
    }

    @Test
    void testGetHorizontalAlignment() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.setHorizontalAlignment(SwingConstants.RIGHT);
        assertThat(operator2.getHorizontalAlignment()).isEqualTo(SwingConstants.RIGHT);
        assertThat(operator2.getHorizontalAlignment()).isEqualTo(onQueue(textField::getHorizontalAlignment));
    }

    @Test
    void testGetScrollOffset() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.setScrollOffset(operator2.getScrollOffset());
    }

    @Test
    void testPostActionEvent() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 =
                JTextFieldOperator.waitFor(operator1, ComponentPredicates.byName("JTextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        operator2.setActionCommand("ACTION_COMMAND");
        ActionListener1 listener = new ActionListener1();
        operator2.addActionListener(listener);
        operator2.postActionEvent();
        assertThat(listener.actionCommand).isEqualTo("ACTION_COMMAND");
    }

    @Test
    void testGetHorizontalVisibility() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JTextFieldOperator operator2 = JTextFieldOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        operator2.getHorizontalVisibility();
    }

    static class ActionListener1 implements ActionListener {
        String actionCommand;

        @Override
        public void actionPerformed(ActionEvent event) {
            actionCommand = event.getActionCommand();
        }
    }
}
