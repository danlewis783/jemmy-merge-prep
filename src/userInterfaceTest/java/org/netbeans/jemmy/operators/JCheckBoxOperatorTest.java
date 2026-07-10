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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JCheckBoxOperatorTest {

    private JCheckBox checkBox;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            checkBox = new JCheckBox("JCheckBoxOperatorTest");
            checkBox.setName("JCheckBoxOperatorTest");
            frame.getContentPane().add(checkBox);
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
        JCheckBoxOperator operator2 = JCheckBoxOperator.waitFor(operator1);
        assertThat(operator2).isNotNull();
        JCheckBoxOperator operator3 =
                JCheckBoxOperator.waitFor(operator1, ComponentPredicates.byName("JCheckBoxOperatorTest"));
        assertThat(operator3).isNotNull();
        JCheckBoxOperator operator4 =
                JCheckBoxOperator.waitFor(operator1, "JCheckBoxOperatorTest", StringComparators.strict());
        assertThat(operator4).isNotNull();
    }

    @Test
    void testFindJCheckBox() {
        JCheckBox checkBox1 =
                JCheckBoxOperator.findJCheckBox(frame, ComponentPredicates.byName("JCheckBoxOperatorTest"));
        assertThat(checkBox1).isNotNull();
        JCheckBox checkBox2 = JCheckBoxOperator.findJCheckBox(
                frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(checkBox2).isNotNull();
    }

    @Test
    void testWaitJCheckBox() {
        JCheckBox checkBox1 =
                JCheckBoxOperator.waitJCheckBox(frame, ComponentPredicates.byName("JCheckBoxOperatorTest"));
        assertThat(checkBox1).isNotNull();
        JCheckBox checkBox2 = JCheckBoxOperator.waitJCheckBox(
                frame, "JCheckBoxOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(checkBox2).isNotNull();
    }
}
