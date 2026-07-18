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
package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_022
@Timeout(value=1, unit=TimeUnit.SECONDS)
class InternalFrameWorkflowTest {

    @Test
    void test() {
        InternalFramesApp.main();

        // main() shows the frame via invokeLater, so a non-waiting find races the EDT
        JFrame frame = JFrameOperator.waitJFrame("InternalFramesApp", StringComparators.substring());
        JInternalFrameOperator frame1Op =
                JInternalFrameOperator.waitFor(JFrameOperator.of(frame), "Frame 1", StringComparators.strict());
        JInternalFrameOperator frame2Op =
                JInternalFrameOperator.waitFor(JFrameOperator.of(frame), "Frame 2", StringComparators.strict());
        JInternalFrameOperator fo = JInternalFrameOperator.waitFor(JFrameOperator.of(frame));
        assertThat((fo.getSource() == frame1Op.getSource()) || (fo.getSource() == frame2Op.getSource()))
                .isTrue();
        frame1Op.deiconify();
        JButtonOperator.waitFor(ContainerOperator.of(frame2Op.getRootPane())).push();
        JButtonOperator.waitFor(ContainerOperator.of(frame1Op.getRootPane())).push();
        frame1Op.iconify();
        frame1Op.deiconify();
        frame1Op.maximize();
        frame1Op.demaximize();
        frame1Op.move(100, 100);
        frame1Op.resize(150, 150);
        frame2Op.iconify();
        frame2Op.deiconify();
        frame2Op.maximize();
        frame2Op.demaximize();
        frame2Op.move(100, 100);
        frame2Op.resize(250, 250);
        testJInternalFrame(frame2Op);
    }

    private void testJInternalFrame(JInternalFrameOperator op) {
        JInternalFrame src = (JInternalFrame) op.getSource();
        assertThat(op.getContentPane()).isEqualTo(onQueue(src::getContentPane));
        assertThat(op.getDefaultCloseOperation()).isEqualTo(onQueue(src::getDefaultCloseOperation));
        assertThat(op.getDesktopIcon()).isEqualTo(onQueue(src::getDesktopIcon));
        assertThat(op.getDesktopPane()).isEqualTo(onQueue(src::getDesktopPane));
        assertThat(op.getFrameIcon()).isEqualTo(onQueue(src::getFrameIcon));
        assertThat(op.getGlassPane()).isEqualTo(onQueue(src::getGlassPane));
        assertThat(op.getJMenuBar()).isEqualTo(onQueue(src::getJMenuBar));
        assertThat(op.getLayer()).isEqualTo(onQueue(src::getLayer));
        assertThat(op.getLayeredPane()).isEqualTo(onQueue(src::getLayeredPane));
        assertThat(op.getTitle()).isEqualTo(onQueue(src::getTitle));
        assertThat(op.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(op.getWarningString()).isEqualTo(onQueue(src::getWarningString));
        assertThat(op.isClosable()).isEqualTo(onQueue(src::isClosable));
        assertThat(op.isClosed()).isEqualTo(onQueue(src::isClosed));
        assertThat(op.isIcon()).isEqualTo(onQueue(src::isIcon));
        assertThat(op.isIconifiable()).isEqualTo(onQueue(src::isIconifiable));
        assertThat(op.isMaximizable()).isEqualTo(onQueue(src::isMaximizable));
        assertThat(op.isMaximum()).isEqualTo(onQueue(src::isMaximum));
        assertThat(op.isResizable()).isEqualTo(onQueue(src::isResizable));
        assertThat(op.isSelected()).isEqualTo(onQueue(src::isSelected));
    }
}
