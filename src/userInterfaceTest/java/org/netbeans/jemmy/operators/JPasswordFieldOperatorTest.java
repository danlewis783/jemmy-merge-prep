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
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JPasswordFieldOperatorTest {

    private JFrame frame;
    private JPasswordField passwordField;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                passwordField = new JPasswordField("JPasswordFieldOperatorTest");
                passwordField.setName("JPasswordFieldOperatorTest");
                frame.getContentPane().add(passwordField);
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
        assertThat(operator1).isNotNull();
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();
        JPasswordFieldOperator operator4 = new JPasswordFieldOperator(operator1);
        assertThat(operator4).isNotNull();
        JPasswordFieldOperator operator5 =
                new JPasswordFieldOperator(operator1, "JPasswordFieldOperatorTest", StringComparators.strict());
        assertThat(operator5).isNotNull();
    }

    @Test
    void testFindJPasswordField() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JPasswordField passwordField1 =
                JPasswordFieldOperator.findJPasswordField(frame, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertThat(passwordField1).isNotNull();
        JPasswordField passwordField2 = JPasswordFieldOperator.findJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(passwordField2).isNotNull();
    }

    @Test
    void testWaitJPasswordField() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JPasswordField passwordField1 =
                JPasswordFieldOperator.waitJPasswordField(frame, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertThat(passwordField1).isNotNull();
        JPasswordField passwordField2 = JPasswordFieldOperator.waitJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(passwordField2).isNotNull();
    }

    @Test
    void testEchoCharIsSet() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();
        assertThat(operator3.echoCharIsSet()).isTrue();
        assertThat(passwordField.echoCharIsSet()).isTrue();
        operator3.setEchoChar('a');
        assertThat(operator3.getEchoChar()).isEqualTo('a');
        assertThat(passwordField.getEchoChar()).isEqualTo(operator3.getEchoChar());
    }

    @Test
    void testGetPassword() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertThat(operator3).isNotNull();

        try {
            EventQueue.invokeAndWait(() -> passwordField.setText("hallo"));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        assertThat(operator3.getPassword()[0]).isEqualTo('h');
        assertThat(operator3.getPassword()[1]).isEqualTo('a');
        assertThat(operator3.getPassword()[2]).isEqualTo('l');
        assertThat(operator3.getPassword()[3]).isEqualTo('l');
        assertThat(operator3.getPassword()[4]).isEqualTo('o');
    }
}
