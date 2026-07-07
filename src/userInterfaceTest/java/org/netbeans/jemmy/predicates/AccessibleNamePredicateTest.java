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
    void after() {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    @Test
    void testCheckContext() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JButtonOperator operator1 = new JButtonOperator(operator, new AccessibleNamePredicate("Accessible"));
        assertThat(operator1).isNotNull();
    }
}
