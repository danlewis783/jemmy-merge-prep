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
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

// UI fixtures are created on the EDT in beforeEach; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JDialogOperatorTest {

    private JDialog dialog;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            jFrame.setVisible(true);
            jFrame.setLocationRelativeTo(null);
            frame = jFrame;
            JDialog jDialog = new JDialog(jFrame, "JDialogOperatorTest");
            jDialog.setName("JDialogOperatorTest");
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
            jDialog.setVisible(true);
            dialog = jDialog;
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            dialog.setVisible(false);
            dialog.dispose();
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void constructor() {
        new JDialogOperator(PredicatesJ.byName("JDialogOperatorTest"));
        new JFrameOperator();
        new JDialogOperator(new JFrameOperator());
        new JFrameOperator();
        new JDialogOperator(new JFrameOperator(), PredicatesJ.byName("JDialogOperatorTest"));
        new JDialogOperator(new JFrameOperator(), "JDialogOperatorTest", StringComparators.strict());
    }

    @Test
    void findJDialog() {
        assertThat(JDialogOperator.findJDialog("JDialogOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JDialogOperator.findJDialog(frame, "JDialogOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JDialogOperator.findJDialog(PredicatesJ.byName("JDialogOperatorTest")))
                .isNotNull();
        assertThat(JDialogOperator.findJDialog(frame, PredicatesJ.byName("JDialogOperatorTest")))
                .isNotNull();
    }

    @Test
    void waitJDialog() {
        assertThat(JDialogOperator.waitJDialog("JDialogOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JDialogOperator.waitJDialog(frame, "JDialogOperatorTest", StringComparators.strict()))
                .isNotNull();
        assertThat(JDialogOperator.waitJDialog(PredicatesJ.byName("JDialogOperatorTest")))
                .isNotNull();
        assertThat(JDialogOperator.waitJDialog(frame, PredicatesJ.byName("JDialogOperatorTest")))
                .isNotNull();
        Future<JDialog> future1 = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable1());
        new JDialogOperator();
        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> future1.get(1000L, TimeUnit.MILLISECONDS))
                .withMessage(null);

        Future<JDialog> future2 = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable2(frame));
        new JDialogOperator();
        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> future2.get(1000L, TimeUnit.MILLISECONDS))
                .withMessage(null);
    }

    @Test
    void waitJDialog_Timeout() {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, 500L)) {
            Future<JDialog> laFutura = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable1());
            new JDialogOperator();
            assertThatExceptionOfType(ExecutionException.class)
                    .isThrownBy(laFutura::get)
                    .withMessageContaining("timeout \"DialogWaiter_WaitDialogTimeout\" (500 ms) exceeded after (");
        }
    }

    @Test
    void getJMenuBar() throws InterruptedException, InvocationTargetException {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JMenuBar> jMenuBar = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> jMenuBar.set(new JMenuBar()));
        operator.setJMenuBar(Objects.requireNonNull(jMenuBar.get()));
        assertThat(dialog.getJMenuBar()).isNotNull();
        assertThat(operator.getJMenuBar()).isNotNull();
    }

    @Test
    void getDefaultCloseOperation() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        assertThat(dialog.getDefaultCloseOperation()).isEqualTo(JDialog.DO_NOTHING_ON_CLOSE);
        assertThat(operator.getDefaultCloseOperation()).isEqualTo(JDialog.DO_NOTHING_ON_CLOSE);
    }

    @Test
    void getContentPane() throws InterruptedException, InvocationTargetException {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JScrollPane> jScrollPane = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> jScrollPane.set(new JScrollPane()));
        operator.setContentPane(Objects.requireNonNull(jScrollPane.get()));
        assertThat(dialog.getContentPane()).isNotNull();
        assertThat(operator.getContentPane()).isNotNull();
    }

    @Test
    void getGlassPane() throws InterruptedException, InvocationTargetException {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JScrollPane> glassPane = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> glassPane.set(new JScrollPane()));
        operator.setGlassPane(Objects.requireNonNull(glassPane.get()));
        assertThat(dialog.getGlassPane()).isNotNull();
        assertThat(operator.getGlassPane()).isNotNull();
    }

    @Test
    void getLayeredPane() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setLayeredPane(new JLayeredPane());
        assertThat(dialog.getLayeredPane()).isNotNull();
        assertThat(operator.getLayeredPane()).isNotNull();
    }

    @Test
    void getRootPane() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        assertThat(dialog.getRootPane()).isNotNull();
        assertThat(operator.getRootPane()).isNotNull();
    }

    @Test
    void getAccessibleContext() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        assertThat(dialog.getAccessibleContext()).isNotNull();
        assertThat(operator.getAccessibleContext()).isNotNull();
    }

    @Test
    void getTopModalDialog() throws InterruptedException, InvocationTargetException {
        GetTopModalDialogRunnable1 runnable1 = new GetTopModalDialogRunnable1(dialog);
        EventQueue.invokeAndWait(runnable1);
        JDialogOperator operator1 = new JDialogOperator();
        assertThat(operator1).isNotNull();
        JDialog dialog1 = (JDialog) JDialogOperator.getTopModalDialog();
        assertThat(dialog1).isNotNull();
        AtomicReference<GetTopModalDialogRunnable2> runnable2 = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> runnable2.set(new GetTopModalDialogRunnable2(dialog)));
        EventQueue.invokeAndWait(runnable2.get());
        JDialogOperator operator2 = new JDialogOperator("JDialogOperatorTest");
        assertThat(operator2).isNotNull();
        JDialog dialog2 = (JDialog) JDialogOperator.getTopModalDialog();
        assertThat(dialog2).isNotNull();
        EventQueue.invokeAndWait(() -> Objects.requireNonNull(runnable2.get()).cleanup());
    }

    @Test
    void setLocationRelativeTo() {
        dialog.setLocation(0, 0);
        dialog.pack();
        int x = dialog.getX();
        int y = dialog.getY();
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setLocationRelativeTo(null);
        assertThat(x != dialog.getX()).isTrue();
        assertThat(operator.getX()).isEqualTo(dialog.getX());
        assertThat(y != dialog.getY()).isTrue();
        assertThat(operator.getY()).isEqualTo(dialog.getY());
    }

    private static class GetTopModalDialogRunnable1 implements Runnable {
        private final JDialog jDialog;

        private GetTopModalDialogRunnable1(JDialog jDialog) {
            this.jDialog = jDialog;
        }

        @Override
        public void run() {
            jDialog.setModal(true);
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
        }
    }

    private static class GetTopModalDialogRunnable2 implements Runnable {
        private final JDialog jDialog;

        private GetTopModalDialogRunnable2(JDialog jDialog) {
            this.jDialog = new JDialog(jDialog, "JDialogOperatorTest2", true);
        }

        @Override
        public void run() {
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
        }

        void cleanup() {
            jDialog.setVisible(false);
            jDialog.dispose();
        }
    }

    private static class WaitJDialogCallable1 implements Callable<JDialog> {
        @Override
        public JDialog call() {
            return JDialogOperator.waitJDialog("YouWontEverFindMe", StringComparators.strict());
        }
    }

    private static class WaitJDialogCallable2 implements Callable<JDialog> {
        private final JFrame frame;

        private WaitJDialogCallable2(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public JDialog call() {
            return JDialogOperator.waitJDialog(frame, PredicatesJ.byName("YouWontEverFindMe"));
        }
    }
}
