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
import java.awt.Frame;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class TextFieldOperatorTest {

    private Frame frame;
    private TextField textField;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new Frame();
            textField = new TextField("TextFieldOperatorTest");
            textField.setName("TextFieldOperatorTest");
            frame.add(textField);
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
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        TextFieldOperator operator2 = new TextFieldOperator(operator, PredicatesJ.byName("TextFieldOperatorTest"));
        assertThat(operator2).isNotNull();
        TextFieldOperator operator3 =
                new TextFieldOperator(operator, "TextFieldOperatorTest", StringComparators.strict());
        assertThat(operator3).isNotNull();
    }

    @Test
    void testFindTextField() {
        TextField textField1 = TextFieldOperator.findTextField(frame, PredicatesJ.byName("TextFieldOperatorTest"));
        assertThat(textField1).isNotNull();
        TextField textField2 = TextFieldOperator.findTextField(
                frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textField2).isNotNull();
    }

    @Test
    void testWaitTextField() {
        TextField textField1 = TextFieldOperator.waitTextField(frame, PredicatesJ.byName("TextFieldOperatorTest"));
        assertThat(textField1).isNotNull();
        TextField textField2 = TextFieldOperator.waitTextField(
                frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textField2).isNotNull();
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        ActionListener listener = event -> {};
        operator1.addActionListener(listener);
        operator1.removeActionListener(listener);
    }

    @Test
    void testEchoCharIsSet() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.echoCharIsSet();
    }

    @Test
    void testGetColumns() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumns(operator1.getColumns());
    }

    @Test
    void testGetEchoChar() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getEchoChar();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getMinimumSize(0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = new FrameOperator();
        assertThat(operator).isNotNull();
        TextFieldOperator operator1 = new TextFieldOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getPreferredSize(0);
    }
}
