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
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
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
            TestWindows.place(frame);
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
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        TextFieldOperator operator2 =
                TextFieldOperator.waitFor(operator, ComponentPredicates.byName("TextFieldOperatorTest"));
        TextFieldOperator operator3 =
                TextFieldOperator.waitFor(operator, "TextFieldOperatorTest", StringComparators.strict());
    }

    @Test
    void testFindTextField() {
        TextField textField1 =
                TextFieldOperator.findTextField(frame, ComponentPredicates.byName("TextFieldOperatorTest"));
        assertThat(textField1).isNotNull();
        TextField textField2 = TextFieldOperator.findTextField(
                frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(textField2).isNotNull();
    }

    @Test
    void testWaitTextField() {
        TextFieldOperator.waitTextField(frame, ComponentPredicates.byName("TextFieldOperatorTest"));
        TextFieldOperator.waitTextField(
                frame, "TextFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testAddActionListener() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        ActionListener listener = event -> {};
        operator1.addActionListener(listener);
        operator1.removeActionListener(listener);
    }

    @Test
    void testEchoCharIsSet() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        operator1.echoCharIsSet();
    }

    @Test
    void testGetColumns() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        operator1.setColumns(operator1.getColumns());
    }

    @Test
    void testGetEchoChar() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        operator1.getEchoChar();
    }

    @Test
    void testGetMinimumSize() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        operator1.getMinimumSize(0);
    }

    @Test
    void testGetPreferredSize() {
        FrameOperator operator = FrameOperator.waitFor();
        TextFieldOperator operator1 = TextFieldOperator.waitFor(operator);
        operator1.getPreferredSize(0);
    }
}
