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
package org.netbeans.jemmy.predicates;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import javax.swing.JButton;
import javax.swing.JFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class AccessibleNamePredicateTest {

    private JButton button;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            button = new JButton("Button");
            button.getAccessibleContext().setAccessibleName("Accessible");
            frame.getContentPane().add(button);
            frame.setLocationRelativeTo(null);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testCheckContext() throws Exception {
        EventQueue.invokeAndWait(() -> frame.setVisible(true));
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JButtonOperator operator1 = JButtonOperator.waitFor(operator, new AccessibleNamePredicate("Accessible"));
        assertThat(operator1).isNotNull();
    }
}
