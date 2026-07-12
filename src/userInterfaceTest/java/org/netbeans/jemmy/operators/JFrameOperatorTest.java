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
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.IndexedFramesApp;
import org.netbeans.jemmy.testing.MenuNavigationApp;
import org.netbeans.jemmy.util.StringComparators;

// mainFrame is assigned inside the EDT lambda in beforeEach, which NullAway cannot trace
@SuppressWarnings({"NullAway.Init", "NotNullFieldNotInitialized"})
class JFrameOperatorTest {
    private JFrame mainFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame = new JFrame("JFrameOperatorTest");
            mainFrame.setName("JFrameOperatorTest");
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
        });
    }

    @AfterEach
    void after() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame.setVisible(false);
            mainFrame.dispose();
        });
    }

    @Test
    void constructor() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();
        JFrameOperator operator2 = JFrameOperator.waitFor("JFrameOperatorTest");
        assertThat(operator2).isNotNull();
        JFrameOperator operator3 = JFrameOperator.waitFor(ComponentPredicates.byName("JFrameOperatorTest"));
        assertThat(operator3).isNotNull();
    }

    @Test
    void findJFrame() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrame frame1 = JFrameOperator.findJFrame(ComponentPredicates.byName("JFrameOperatorTest"));
        assertThat(frame1).isNotNull();
        JFrame frame2 = JFrameOperator.findJFrame("JFrameOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(frame2).isNotNull();
    }

    @Test
    void waitJFrame() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrame frame1 = JFrameOperator.waitJFrame(ComponentPredicates.byName("JFrameOperatorTest"));
        assertThat(frame1).isNotNull();
        JFrame frame2 = JFrameOperator.waitJFrame("JFrameOperatorTest");
        assertThat(frame2).isNotNull();
        WaitJFrameCallable callable = new WaitJFrameCallable();
        Future<JFrame> future = Executors.newSingleThreadExecutor().submit(callable);
        JFrameOperator operator1 = JFrameOperator.waitFor();
        assertThat(operator1).isNotNull();

        assertThatExceptionOfType(TimeoutException.class).isThrownBy(() -> future.get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    void getAccessibleContext() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        assertThat(operator.getAccessibleContext()).isNotNull();
    }

    @Test
    void getContentPane() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JScrollPane scrollPane = onQueue(JScrollPane::new);
        operator.setContentPane(scrollPane);
        assertThat(onQueue(mainFrame::getContentPane)).isEqualTo(scrollPane);
        assertThat(operator.getContentPane()).isNotNull();
    }

    @Test
    void getDefaultCloseOperation() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        operator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        assertThat(onQueue(mainFrame::getDefaultCloseOperation)).isEqualTo(JFrame.EXIT_ON_CLOSE);
        assertThat(operator.getDefaultCloseOperation()).isEqualTo(JFrame.EXIT_ON_CLOSE);
    }

    @Test
    void getGlassPane() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JScrollPane scrollPane = onQueue(JScrollPane::new);
        operator.setGlassPane(scrollPane);
        assertThat(onQueue(mainFrame::getGlassPane)).isEqualTo(scrollPane);
        assertThat(operator.getGlassPane()).isNotNull();
    }

    @Test
    void getJMenuBar() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JMenuBar menuBar = onQueue(JMenuBar::new);
        operator.setJMenuBar(menuBar);
        assertThat(onQueue(mainFrame::getJMenuBar)).isEqualTo(menuBar);
        assertThat(operator.getJMenuBar()).isEqualTo(menuBar);
    }

    @Test
    void getLayeredPane() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        JLayeredPane layeredPane = onQueue(JLayeredPane::new);
        operator.setLayeredPane(layeredPane);
        assertThat(onQueue(mainFrame::getLayeredPane)).isEqualTo(layeredPane);
        assertThat(operator.getLayeredPane()).isNotNull();
    }

    @Test
    void getRootPane() throws InterruptedException, InvocationTargetException {
        showMainFrame();
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator).isNotNull();
        assertThat(operator.getRootPane()).isEqualTo(onQueue(mainFrame::getRootPane));
    }

    // formerly scenario test jemmy_009
    @Test
    void waitJFrameByIndexAndRetitle() throws InterruptedException, InvocationTargetException {
        IndexedFramesApp.main();
        QueueTool.getInstance().waitEmpty();
        try {
            JFrame frm0 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring());
            assertThat(((IndexedFramesApp) frm0).getIndex()).isEqualTo(0);
            JFrame frm1 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring(), 1);
            assertThat(((IndexedFramesApp) frm1).getIndex()).isEqualTo(1);
            JFrame frm2 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring(), 2);
            assertThat(((IndexedFramesApp) frm2).getIndex()).isEqualTo(2);
            JFrameOperator frm2o = JFrameOperator.of(frm2);
            frm2o.setTitle("New Title");
            frm2o.waitTitle("New Title", StringComparators.strict());
        } finally {
            disposeApplicationFrames();
        }
    }

    // formerly scenario test jemmy_038
    @Test
    void frameLifecycleSequence() throws InterruptedException, InvocationTargetException {
        MenuNavigationApp.main();
        try {
            JFrame win = JFrameOperator.waitJFrame("MenuNavigationApp");
            JFrameOperator fo = JFrameOperator.of(win);
            fo.activate();
            fo.resize(400, 400);
            fo.move(200, 200);
            fo.maximize();
            fo.demaximize();
            fo.iconify();
            fo.deiconify();
            fo.requestClose();
        } finally {
            disposeApplicationFrames();
        }
    }

    private void showMainFrame() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> mainFrame.setVisible(true));
    }

    /** Runs the callable on the event dispatch thread so Swing component state is only touched there. */
    private static <T> T onQueue(Callable<T> callable) {
        return QueueTool.getInstance().callOnQueue(Caller.of(callable));
    }

    private void disposeApplicationFrames() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            for (Frame frame : Frame.getFrames()) {
                if (frame != mainFrame) {
                    frame.setVisible(false);
                    frame.dispose();
                }
            }
        });
    }

    private static class WaitJFrameCallable implements Callable<JFrame> {
        @Override
        public JFrame call() {
            return JFrameOperator.waitJFrame("YouWontEverFindMe");
        }
    }
}
