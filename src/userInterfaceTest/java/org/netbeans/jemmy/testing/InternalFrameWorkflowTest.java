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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_022
class InternalFrameWorkflowTest {

    @Test
    void test() {
        InternalFramesApp.main(new String[] {});
        JFrame frame = JFrameOperator.findJFrame("InternalFramesApp", StringComparators.substring());
        JInternalFrameOperator frame1Op =
                new JInternalFrameOperator(new JFrameOperator(frame), "Frame 1", StringComparators.strict());
        JInternalFrameOperator frame2Op =
                new JInternalFrameOperator(new JFrameOperator(frame), "Frame 2", StringComparators.strict());
        JInternalFrameOperator fo = new JInternalFrameOperator(new JFrameOperator(frame));
        assertTrue((fo.getSource() == frame1Op.getSource()) || (fo.getSource() == frame2Op.getSource()));
        frame1Op.deiconify();
        new JButtonOperator(new ContainerOperator(frame2Op.getRootPane())).push();
        new JButtonOperator(new ContainerOperator(frame1Op.getRootPane())).push();
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
        assertTrue(((src.getContentPane() == null) && (op.getContentPane() == null))
                || src.getContentPane().equals(op.getContentPane()));
        assertEquals(src.getDefaultCloseOperation(), op.getDefaultCloseOperation());
        assertTrue(((src.getDesktopIcon() == null) && (op.getDesktopIcon() == null))
                || src.getDesktopIcon().equals(op.getDesktopIcon()));
        assertTrue(((src.getDesktopPane() == null) && (op.getDesktopPane() == null))
                || src.getDesktopPane().equals(op.getDesktopPane()));
        assertTrue(((src.getFrameIcon() == null) && (op.getFrameIcon() == null))
                || src.getFrameIcon().equals(op.getFrameIcon()));
        assertTrue(((src.getGlassPane() == null) && (op.getGlassPane() == null))
                || src.getGlassPane().equals(op.getGlassPane()));
        assertTrue(((src.getJMenuBar() == null) && (op.getJMenuBar() == null))
                || src.getJMenuBar().equals(op.getJMenuBar()));
        assertEquals(src.getLayer(), op.getLayer());
        assertTrue(((src.getLayeredPane() == null) && (op.getLayeredPane() == null))
                || src.getLayeredPane().equals(op.getLayeredPane()));
        assertTrue(((src.getTitle() == null) && (op.getTitle() == null))
                || src.getTitle().equals(op.getTitle()));
        assertTrue(
                ((src.getUI() == null) && (op.getUI() == null)) || src.getUI().equals(op.getUI()));
        assertTrue(((src.getWarningString() == null) && (op.getWarningString() == null))
                || src.getWarningString().equals(op.getWarningString()));
        assertEquals(src.isClosable(), op.isClosable());
        assertEquals(src.isClosed(), op.isClosed());
        assertEquals(src.isIcon(), op.isIcon());
        assertEquals(src.isIconifiable(), op.isIconifiable());
        assertEquals(src.isMaximizable(), op.isMaximizable());
        assertEquals(src.isMaximum(), op.isMaximum());
        assertEquals(src.isResizable(), op.isResizable());
        assertEquals(src.isSelected(), op.isSelected());
    }
}
