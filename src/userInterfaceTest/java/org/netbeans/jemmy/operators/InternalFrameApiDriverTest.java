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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.DriverType;
import org.netbeans.jemmy.drivers.windows.InternalFrameAPIDriver;

/**
 * Exercises internal frames through {@link InternalFrameAPIDriver}, the look-and-feel-immune driver. Installs the
 * driver for this JVM only; the UI test suite forks one JVM per class, so the default drivers are unaffected
 * elsewhere.
 */
// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class InternalFrameApiDriverTest {

    private JFrame frame;
    private JInternalFrame internalFrame;

    @BeforeAll
    static void beforeAll() {
        DriverManager driverManager = DriverManager.newInstance(JemmyContext.getInstance());
        InternalFrameAPIDriver driver = new InternalFrameAPIDriver();
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
            internalFrame = new JInternalFrame("InternalFrameApiDriverTest", true, true, true, true);
            internalFrame.setName("InternalFrameApiDriverTest");
            internalFrame.setSize(100, 100);
            internalFrame.setVisible(true);
            desktop.add(internalFrame);
            frame.setSize(300, 300);
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
    void maximizeAndDemaximize() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.maximize();
        assertThat(operator.isMaximum()).isTrue();
        operator.demaximize();
        assertThat(operator.isMaximum()).isFalse();
    }

    @Test
    void iconifyAndDeiconify() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.iconify();
        assertThat(operator.isIcon()).isTrue();
        operator.deiconify();
        assertThat(operator.isIcon()).isFalse();
    }

    @Test
    void activateSelectsTheFrame() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.activate();
        assertThat(operator.isSelected()).isTrue();
    }

    @Test
    void moveAndResize() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.move(40, 30);
        assertThat(operator.getX()).isEqualTo(40);
        assertThat(operator.getY()).isEqualTo(30);
        operator.resize(150, 120);
        assertThat(operator.getWidth()).isEqualTo(150);
        assertThat(operator.getHeight()).isEqualTo(120);
    }

    @Test
    void closeClosesTheFrame() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        operator.close();
        assertThat(operator.isClosed()).isTrue();
    }

    @Test
    void titleButtonsAreUnsupported() {
        JInternalFrameOperator operator = new JInternalFrameOperator(new JFrameOperator(frame));
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(operator::getMinimizeButton)
                .withMessageContaining("title pane");
    }
}
