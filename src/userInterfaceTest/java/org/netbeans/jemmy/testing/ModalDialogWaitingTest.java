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
import static org.junit.jupiter.api.Assertions.assertSame;

import java.awt.Dialog;
import java.awt.EventQueue;
import javax.swing.JDialog;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_010
class ModalDialogWaitingTest {
    private static final String TITLE = "StagedDialogsApp";

    private StagedDialogsApp appInstance0;
    private StagedDialogsApp appInstance1;
    private StagedDialogsApp appInstance2;

    @Test
    void test() throws Exception {
        EventQueue.invokeAndWait(() -> {
            appInstance0 = new StagedDialogsApp(0);
            appInstance1 = new StagedDialogsApp(1);
            appInstance2 = new StagedDialogsApp(2);
        });
        EventQueue.invokeLater(() -> {
            appInstance0.setVisible(true);
            appInstance1.setVisible(true);
            appInstance2.setVisible(true);
        });
        EventQueue.invokeAndWait(() -> {});
        JDialog jDialog0 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring());
        JDialogOperator fo = new JDialogOperator(jDialog0);
        JDialogOperator fo2 = new JDialogOperator();
        DialogOperator fo3 = new DialogOperator();
        assertSame(fo2.getSource(), fo.getSource());
        assertSame(fo3.getSource(), fo.getSource());
        assertEquals(0, ((StagedDialogsApp) jDialog0).getIndex());
        JDialog jDialog1 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 1);
        assertEquals(1, ((StagedDialogsApp) jDialog1).getIndex());
        JDialog jDialog2 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 2);
        assertEquals(2, ((StagedDialogsApp) jDialog2).getIndex());
        fo = new JDialogOperator(jDialog2);
        fo2 = new JDialogOperator(2);
        fo3 = new DialogOperator(2);
        assertSame(fo2.getSource(), fo.getSource());
        assertSame(fo3.getSource(), fo.getSource());
        assertEquals(2, ((StagedDialogsApp) jDialog2).getIndex());
        testDialog(new JDialogOperator(jDialog2));
        testJDialog(new JDialogOperator(jDialog2));
        JDialogOperator frm2o = new JDialogOperator(jDialog2);
        frm2o.setTitle("New Title");
        frm2o.waitTitle("New Title", StringComparators.strict());
    }

    private void testDialog(DialogOperator op) {
        Dialog src = (Dialog) op.getSource();
        assertEquals(src.getTitle(), op.getTitle());
        assertEquals(src.isModal(), op.isModal());
        assertEquals(src.isResizable(), op.isResizable());
    }

    private void testJDialog(JDialogOperator op) {
        JDialog src = (JDialog) op.getSource();
        assertEquals(src.getAccessibleContext(), op.getAccessibleContext());
        assertEquals(src.getContentPane(), op.getContentPane());
        assertEquals(src.getDefaultCloseOperation(), op.getDefaultCloseOperation());
        assertEquals(src.getGlassPane(), op.getGlassPane());
        assertEquals(src.getJMenuBar(), op.getJMenuBar());
        assertEquals(src.getLayeredPane(), op.getLayeredPane());
        assertEquals(src.getRootPane(), op.getRootPane());
    }
}
