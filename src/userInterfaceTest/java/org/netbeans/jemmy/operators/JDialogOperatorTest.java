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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
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

class JDialogOperatorTest {

    private final AtomicReference<JDialog> jDialogRef = new AtomicReference<>();
    private final AtomicReference<JFrame> jFrameRef = new AtomicReference<>();

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame();
            jFrame.setVisible(true);
            jFrame.setLocationRelativeTo(null);
            jFrameRef.set(jFrame);
            JDialog jDialog = new JDialog(jFrame, "JDialogOperatorTest");
            jDialog.setName("JDialogOperatorTest");
            jDialog.pack();
            jDialog.setLocationRelativeTo(null);
            jDialog.setVisible(true);
            jDialogRef.set(jDialog);
        });
    }

    @AfterEach
    void after() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JDialog jDialog = jDialogRef.get();
            jDialog.setVisible(false);
            jDialog.dispose();
            jDialogRef.set(null);
            JFrame jFrame = jFrameRef.get();
            jFrame.setVisible(false);
            jFrame.dispose();
            jFrameRef.set(null);
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
        assertNotNull(JDialogOperator.findJDialog("JDialogOperatorTest", StringComparators.strict()));
        assertNotNull(JDialogOperator.findJDialog(jFrameRef.get(), "JDialogOperatorTest", StringComparators.strict()));
        assertNotNull(JDialogOperator.findJDialog(PredicatesJ.byName("JDialogOperatorTest")));
        assertNotNull(JDialogOperator.findJDialog(jFrameRef.get(), PredicatesJ.byName("JDialogOperatorTest")));
    }

    @Test
    void waitJDialog() {
        assertNotNull(JDialogOperator.waitJDialog("JDialogOperatorTest", StringComparators.strict()));
        assertNotNull(JDialogOperator.waitJDialog(jFrameRef.get(), "JDialogOperatorTest", StringComparators.strict()));
        assertNotNull(JDialogOperator.waitJDialog(PredicatesJ.byName("JDialogOperatorTest")));
        assertNotNull(JDialogOperator.waitJDialog(jFrameRef.get(), PredicatesJ.byName("JDialogOperatorTest")));
        Future<JDialog> future1 = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable1());
        new JDialogOperator();
        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> future1.get(1000L, TimeUnit.MILLISECONDS))
                .withMessage(null);

        Future<JDialog> future2 = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable2(jFrameRef.get()));
        new JDialogOperator();
        assertThatExceptionOfType(TimeoutException.class)
                .isThrownBy(() -> future2.get(1000L, TimeUnit.MILLISECONDS))
                .withMessage(null);
    }

    @Test
    void waitJDialog_Timeout() {
        TimeoutOverride override = Timeouts.override(TimeoutKey.DialogWaiter_WaitDialogTimeout, 500L);
        Future<JDialog> future1 = Executors.newSingleThreadExecutor().submit(new WaitJDialogCallable1());
        new JDialogOperator();

        try {
            assertThatExceptionOfType(ExecutionException.class)
                    .isThrownBy(future1::get)
                    .withMessageContaining("timeout \"DialogWaiter_WaitDialogTimeout\" (500 ms) exceeded after (");
        } finally {
            override.cancel();
        }
    }

    @Test
    void getJMenuBar() throws Exception {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JMenuBar> jMenuBar = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> jMenuBar.set(new JMenuBar()));
        operator.setJMenuBar(jMenuBar.get());
        assertNotNull(jDialogRef.get().getJMenuBar());
        assertNotNull(operator.getJMenuBar());
    }

    @Test
    void getDefaultCloseOperation() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        assertEquals(JDialog.DO_NOTHING_ON_CLOSE, jDialogRef.get().getDefaultCloseOperation());
        assertEquals(JDialog.DO_NOTHING_ON_CLOSE, operator.getDefaultCloseOperation());
    }

    @Test
    void getContentPane() throws Exception {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JScrollPane> jScrollPane = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> jScrollPane.set(new JScrollPane()));
        operator.setContentPane(jScrollPane.get());
        assertNotNull(jDialogRef.get().getContentPane());
        assertNotNull(operator.getContentPane());
    }

    @Test
    void getGlassPane() throws Exception {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        AtomicReference<JScrollPane> glassPane = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> glassPane.set(new JScrollPane()));
        operator.setGlassPane(glassPane.get());
        assertNotNull(jDialogRef.get().getGlassPane());
        assertNotNull(operator.getGlassPane());
    }

    @Test
    void getLayeredPane() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setLayeredPane(new JLayeredPane());
        assertNotNull(jDialogRef.get().getLayeredPane());
        assertNotNull(operator.getLayeredPane());
    }

    @Test
    void getRootPane() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        assertNotNull(jDialogRef.get().getRootPane());
        assertNotNull(operator.getRootPane());
    }

    @Test
    void getAccessibleContext() {
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        assertNotNull(jDialogRef.get().getAccessibleContext());
        assertNotNull(operator.getAccessibleContext());
    }

    @Test
    void getTopModalDialog() throws Exception {
        GetTopModalDialogRunnable1 runnable1 = new GetTopModalDialogRunnable1(jDialogRef.get());
        EventQueue.invokeAndWait(runnable1);
        JDialogOperator operator1 = new JDialogOperator();
        assertNotNull(operator1);
        JDialog dialog1 = (JDialog) JDialogOperator.getTopModalDialog();
        assertNotNull(dialog1);
        AtomicReference<GetTopModalDialogRunnable2> runnable2 = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> runnable2.set(new GetTopModalDialogRunnable2(jDialogRef.get())));
        EventQueue.invokeAndWait(runnable2.get());
        JDialogOperator operator2 = new JDialogOperator("JDialogOperatorTest");
        assertNotNull(operator2);
        JDialog dialog2 = (JDialog) JDialogOperator.getTopModalDialog();
        assertNotNull(dialog2);
        EventQueue.invokeAndWait(() -> runnable2.get().cleanup());
    }

    @Test
    void setLocationRelativeTo() {
        jDialogRef.get().setLocation(0, 0);
        jDialogRef.get().pack();
        int x = jDialogRef.get().getX();
        int y = jDialogRef.get().getY();
        JDialogOperator operator = new JDialogOperator("JDialogOperatorTest");
        operator.setLocationRelativeTo(null);
        assertTrue(x != jDialogRef.get().getX());
        assertEquals(jDialogRef.get().getX(), operator.getX());
        assertTrue(y != jDialogRef.get().getY());
        assertEquals(jDialogRef.get().getY(), operator.getY());
    }

    private static class GetTopModalDialogRunnable1 implements Runnable {
        private JDialog jDialog;

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
        private JDialog jDialog;

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
            jDialog = null;
        }
    }

    private static class WaitJDialogCallable1 implements Callable<JDialog> {
        @Override
        public JDialog call() {
            return JDialogOperator.waitJDialog("YouWontEverFindMe", StringComparators.strict());
        }
    }

    private static class WaitJDialogCallable2 implements Callable<JDialog> {
        private JFrame frame;

        private WaitJDialogCallable2(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public JDialog call() {
            return JDialogOperator.waitJDialog(frame, PredicatesJ.byName("YouWontEverFindMe"));
        }
    }
}
