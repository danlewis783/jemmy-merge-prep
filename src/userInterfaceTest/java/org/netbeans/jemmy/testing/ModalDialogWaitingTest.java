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

import java.awt.Dialog;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDialog;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_010
@Timeout(value=1, unit=TimeUnit.SECONDS)
class ModalDialogWaitingTest {
    private static final String TITLE = "StagedDialogsApp";

    private StagedDialogsApp appInstance0;
    private StagedDialogsApp appInstance1;
    private StagedDialogsApp appInstance2;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            appInstance0 = new StagedDialogsApp(0);
            appInstance1 = new StagedDialogsApp(1);
            appInstance2 = new StagedDialogsApp(2);
            appInstance0.setVisible(true);
            appInstance1.setVisible(true);
            appInstance2.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            appInstance0.setVisible(false);
            appInstance0.dispose();
            appInstance1.setVisible(false);
            appInstance1.dispose();
            appInstance2.setVisible(false);
            appInstance2.dispose();
        });
    }

    @Test
    void test() {
        JDialog jDialog0 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring());
        JDialogOperator fo = JDialogOperator.of(jDialog0);
        JDialogOperator fo2 = JDialogOperator.waitFor();
        DialogOperator fo3 = DialogOperator.waitFor();
        assertThat(fo.getSource()).isSameAs(fo2.getSource());
        assertThat(fo.getSource()).isSameAs(fo3.getSource());
        assertThat(((StagedDialogsApp) jDialog0).getIndex()).isEqualTo(0);
        JDialog jDialog1 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 1);
        assertThat(((StagedDialogsApp) jDialog1).getIndex()).isEqualTo(1);
        JDialog jDialog2 = JDialogOperator.waitJDialog(TITLE, StringComparators.substring(), 2);
        assertThat(((StagedDialogsApp) jDialog2).getIndex()).isEqualTo(2);
        fo = JDialogOperator.of(jDialog2);
        fo2 = JDialogOperator.waitFor(2);
        fo3 = DialogOperator.waitFor(2);
        assertThat(fo.getSource()).isSameAs(fo2.getSource());
        assertThat(fo.getSource()).isSameAs(fo3.getSource());
        assertThat(((StagedDialogsApp) jDialog2).getIndex()).isEqualTo(2);
        testDialog(JDialogOperator.of(jDialog2));
        testJDialog(JDialogOperator.of(jDialog2));
        JDialogOperator frm2o = JDialogOperator.of(jDialog2);
        frm2o.setTitle("New Title");
        frm2o.waitTitle("New Title", StringComparators.strict());
    }

    private void testDialog(DialogOperator op) {
        Dialog src = (Dialog) op.getSource();
        assertThat(op.getTitle()).isEqualTo(onQueue(src::getTitle));
        assertThat(op.isModal()).isEqualTo(onQueue(src::isModal));
        assertThat(op.isResizable()).isEqualTo(onQueue(src::isResizable));
    }

    private void testJDialog(JDialogOperator op) {
        JDialog src = (JDialog) op.getSource();
        assertThat(op.getAccessibleContext()).isEqualTo(onQueue(src::getAccessibleContext));
        assertThat(op.getContentPane()).isEqualTo(onQueue(src::getContentPane));
        assertThat(op.getDefaultCloseOperation()).isEqualTo(onQueue(src::getDefaultCloseOperation));
        assertThat(op.getGlassPane()).isEqualTo(onQueue(src::getGlassPane));
        assertThat(op.getJMenuBar()).isEqualTo(onQueue(src::getJMenuBar));
        assertThat(op.getLayeredPane()).isEqualTo(onQueue(src::getLayeredPane));
        assertThat(op.getRootPane()).isEqualTo(onQueue(src::getRootPane));
    }

    private static class StagedDialogsApp extends JDialog {
        private final int index;

        StagedDialogsApp(int index) {
            super.setTitle("StagedDialogsApp/" + index);
            this.index = index;
            setSize(300, 300);
            TestWindows.place(this, index);
        }

        int getIndex() {
            return index;
        }
    }
}
