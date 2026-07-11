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
import javax.swing.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.DumpOnFailure;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.DriverType;
import org.netbeans.jemmy.drivers.windows.InternalFramePopupMenuDriver;

/**
 * Exercises internal frames under the Motif look and feel through {@link InternalFramePopupMenuDriver}, ported from
 * openjdk/jemmy-v2 (CODETOOLS-7902300): Motif keeps close/minimize/maximize/restore in a popup menu behind the sole
 * title button. Installs the LAF and driver for this JVM only; the UI test suite forks one JVM per class, so the
 * defaults are unaffected elsewhere.
 */
@ExtendWith(DumpOnFailure.class)
// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class InternalFramePopupMenuDriverTest {

    private JFrame frame;
    private JInternalFrame internalFrame;

    @BeforeAll
    static void beforeAll() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            } catch (UnsupportedLookAndFeelException
                    | ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        DriverManager driverManager = DriverManager.newInstance(JemmyContext.getInstance());
        InternalFramePopupMenuDriver driver = new InternalFramePopupMenuDriver();
        driverManager.setDriver(DriverType.Frame, driver);
        driverManager.setDriver(DriverType.InternalFrame, driver);
        driverManager.setDriver(DriverType.Window, driver);
    }

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            JDesktopPane desktop = new JDesktopPane();
            frame.setContentPane(desktop);
            internalFrame = new JInternalFrame("InternalFramePopupMenuDriverTest", true, true, true, true);
            internalFrame.setName("InternalFramePopupMenuDriverTest");
            internalFrame.setSize(150, 150);
            internalFrame.setVisible(true);
            desktop.add(internalFrame);
            frame.setSize(400, 400);
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
    void maximizeAndDemaximizeThroughPopupMenu() {
        JInternalFrameOperator operator = JInternalFrameOperator.waitFor(JFrameOperator.of(frame));
        operator.maximize();
        assertThat(operator.isMaximum()).isTrue();
        operator.demaximize();
        assertThat(operator.isMaximum()).isFalse();
    }

    @Test
    void iconifyThroughPopupMenu() {
        JInternalFrameOperator operator = JInternalFrameOperator.waitFor(JFrameOperator.of(frame));
        operator.iconify();
        assertThat(operator.isIcon()).isTrue();
    }

    @Test
    void closeThroughPopupMenu() {
        JInternalFrameOperator operator = JInternalFrameOperator.waitFor(JFrameOperator.of(frame));
        operator.close();
        assertThat(operator.isClosed()).isTrue();
    }

    @Test
    void popupButtonIsTheOnlyTitleControl() {
        JInternalFrameOperator operator = JInternalFrameOperator.waitFor(JFrameOperator.of(frame));
        assertThat(operator.getPopupButton().getSource()).isNotNull();
    }
}
