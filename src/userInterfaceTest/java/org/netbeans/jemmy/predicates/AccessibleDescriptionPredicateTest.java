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
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JWindow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.WindowOperator;
import org.netbeans.jemmy.testing.TestWindows;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class AccessibleDescriptionPredicateTest {

    private JButton button;
    private JDialog dialog;
    private JFrame frame;
    private JWindow window;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            frame.getAccessibleContext().setAccessibleDescription("Frame");
            button = new JButton("Button");
            button.getAccessibleContext().setAccessibleDescription("Accessible");
            frame.getContentPane().add(button);
            TestWindows.place(frame);
            frame.setVisible(true);
            dialog = new JDialog();
            dialog.getAccessibleContext().setAccessibleDescription("Dialog");
            TestWindows.place(dialog, 1);
            dialog.setVisible(true);
            window = new JWindow(frame);
            window.getAccessibleContext().setAccessibleDescription("Window");
            TestWindows.place(window, 2);
            window.setVisible(true);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
            dialog.setVisible(false);
            dialog.dispose();
            window.setVisible(false);
            window.dispose();
        });
    }

    @Test
    void testCheckContext() {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JButtonOperator operator1 = JButtonOperator.waitFor(operator, new AccessibleDescriptionPredicate("Accessible"));
        assertThat(operator1).isNotNull();
        JDialogOperator operator2 = JDialogOperator.waitFor(new AccessibleDescriptionPredicate("Dialog"));
        assertThat(operator2).isNotNull();
        JFrameOperator operator3 = JFrameOperator.waitFor(new AccessibleDescriptionPredicate("Frame"));
        assertThat(operator3).isNotNull();
        WindowOperator operator4 = WindowOperator.waitFor(operator, new AccessibleDescriptionPredicate("Window"));
        assertThat(operator4).isNotNull();
    }
}
