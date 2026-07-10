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
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JPasswordFieldOperatorTest {

    private JFrame frame;
    private JPasswordField passwordField;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            passwordField = new JPasswordField("JPasswordFieldOperatorTest");
            passwordField.setName("JPasswordFieldOperatorTest");
            frame.getContentPane().add(passwordField);
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
        JPasswordFieldOperator operator3 =
                JPasswordFieldOperator.waitFor(operator1, ComponentPredicates.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();
        JPasswordFieldOperator operator4 = JPasswordFieldOperator.waitFor(operator1);
        assertThat(operator4).isNotNull();
        JPasswordFieldOperator operator5 =
                JPasswordFieldOperator.waitFor(operator1, "JPasswordFieldOperatorTest", StringComparators.strict());
        assertThat(operator5).isNotNull();
    }

    @Test
    void testFindJPasswordField() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JPasswordField passwordField1 = JPasswordFieldOperator.findJPasswordField(
                frame, ComponentPredicates.byName("JPasswordFieldOperatorTest"));
        assertThat(passwordField1).isNotNull();
        JPasswordField passwordField2 = JPasswordFieldOperator.findJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(passwordField2).isNotNull();
    }

    @Test
    void testWaitJPasswordField() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JPasswordField passwordField1 = JPasswordFieldOperator.waitJPasswordField(
                frame, ComponentPredicates.byName("JPasswordFieldOperatorTest"));
        assertThat(passwordField1).isNotNull();
        JPasswordField passwordField2 = JPasswordFieldOperator.waitJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(passwordField2).isNotNull();
    }

    @Test
    void testEchoCharIsSet() {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JPasswordFieldOperator operator3 =
                JPasswordFieldOperator.waitFor(operator1, ComponentPredicates.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();
        assertThat(operator3.echoCharIsSet()).isTrue();
        assertThat(passwordField.echoCharIsSet()).isTrue();
        operator3.setEchoChar('a');
        assertThat(operator3.getEchoChar()).isEqualTo('a');
        assertThat(passwordField.getEchoChar()).isEqualTo(operator3.getEchoChar());
    }

    @Test
    void testGetPassword() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JPasswordFieldOperator operator3 =
                JPasswordFieldOperator.waitFor(operator1, ComponentPredicates.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();

        EventQueue.invokeAndWait(() -> passwordField.setText("hallo"));

        assertThat(operator3.getPassword()[0]).isEqualTo('h');
        assertThat(operator3.getPassword()[1]).isEqualTo('a');
        assertThat(operator3.getPassword()[2]).isEqualTo('l');
        assertThat(operator3.getPassword()[3]).isEqualTo('l');
        assertThat(operator3.getPassword()[4]).isEqualTo('o');
    }
}
