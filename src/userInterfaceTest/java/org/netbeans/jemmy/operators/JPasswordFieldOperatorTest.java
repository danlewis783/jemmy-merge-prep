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
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertNotNull(operator1);
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertNotNull(operator3);
        JPasswordFieldOperator operator4 = new JPasswordFieldOperator(operator1);
        assertNotNull(operator4);
        JPasswordFieldOperator operator5 =
                new JPasswordFieldOperator(operator1, "JPasswordFieldOperatorTest", StringComparators.strict());
        assertNotNull(operator5);
    }

    @Test
    void testFindJPasswordField() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JPasswordField passwordField1 =
                JPasswordFieldOperator.findJPasswordField(frame, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertNotNull(passwordField1);
        JPasswordField passwordField2 = JPasswordFieldOperator.findJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(passwordField2);
    }

    @Test
    void testWaitJPasswordField() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JPasswordField passwordField1 =
                JPasswordFieldOperator.waitJPasswordField(frame, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertNotNull(passwordField1);
        JPasswordField passwordField2 = JPasswordFieldOperator.waitJPasswordField(
                frame, "JPasswordFieldOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(passwordField2);
    }

    @Test
    void testEchoCharIsSet() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertNotNull(operator3);
        assertTrue(operator3.echoCharIsSet());
        assertTrue(passwordField.echoCharIsSet());
        operator3.setEchoChar('a');
        assertEquals('a', operator3.getEchoChar());
        assertEquals(operator3.getEchoChar(), passwordField.getEchoChar());
    }

    @Test
    void testGetPassword() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JPasswordFieldOperator operator3 =
                new JPasswordFieldOperator(operator1, PredicatesJ.byName("JPasswordFieldOperatorTest"));
        assertNotNull(operator3);

        try {
            EventQueue.invokeAndWait(() -> passwordField.setText("hallo"));
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        assertEquals(operator3.getPassword()[0], 'h');
        assertEquals(operator3.getPassword()[1], 'a');
        assertEquals(operator3.getPassword()[2], 'l');
        assertEquals(operator3.getPassword()[3], 'l');
        assertEquals(operator3.getPassword()[4], 'o');
    }
}
