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
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.netbeans.jemmy.util.LookAndFeel;

/**
 * Verifies the tooltip-based internal frame title button lookup under every installed look and feel — the lookup was
 * ported from openjdk/jemmy-v2 precisely because the previous positional lookup was LAF-order dependent. Motif is
 * skipped: it keeps title actions in a popup menu instead of buttons (covered by
 * {@link InternalFramePopupMenuDriverTest}).
 */
// UI fixtures are created on the EDT inside each test; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class InternalFrameTitleButtonsLafTest {

    private JFrame frame;
    private JInternalFrame internalFrame;

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            if (frame != null) {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    @ParameterizedTest
    @MethodSource("org.netbeans.jemmy.testing.LookAndFeelProvider#availableLookAndFeels")
    void titleButtonsWork(String lookAndFeel) throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(lookAndFeel);
            } catch (UnsupportedLookAndFeelException
                    | ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            frame = new JFrame();
            JDesktopPane desktop = new JDesktopPane();
            frame.setContentPane(desktop);
            internalFrame = new JInternalFrame("InternalFrameTitleButtonsLafTest", true, true, true, true);
            internalFrame.setName("InternalFrameTitleButtonsLafTest");
            internalFrame.setSize(150, 150);
            internalFrame.setVisible(true);
            desktop.add(internalFrame);
            frame.setSize(400, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        assumeFalse(LookAndFeel.isMotif(), "Motif keeps title actions in a popup menu, not title buttons");

        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.getMaximizeButton().push();
        operator.waitMaximum(true);
        operator.getCloseButton().push();
        operator.waitClosed();
        assertThat(operator.isClosed()).as("close button under %s", lookAndFeel).isTrue();
    }
}
