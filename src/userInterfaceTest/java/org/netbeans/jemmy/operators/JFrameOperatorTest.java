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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.EventQueue;
import java.awt.Frame;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.IndexedFramesApp;
import org.netbeans.jemmy.testing.MenuNavigationApp;
import org.netbeans.jemmy.util.StringComparators;

class JFrameOperatorTest {
    private AtomicReference<JFrame> jFrameRef = new AtomicReference<>();

    @BeforeEach
    void beforeEach() {
        JFrame jFrame = new JFrame("JFrameOperatorTest");
        jFrame.setName("JFrameOperatorTest");
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrameRef.set(jFrame);
    }

    @AfterEach
    void after() {
        JFrame jFrame = jFrameRef.get();
        jFrame.setVisible(false);
        jFrame.dispose();
        jFrameRef.set(null);
    }

    @Test
    void constructor() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JFrameOperator operator2 = new JFrameOperator("JFrameOperatorTest");
        assertNotNull(operator2);
        JFrameOperator operator3 = new JFrameOperator(PredicatesJ.byName("JFrameOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void gindJFrame() {
        jFrameRef.get().setVisible(true);
        JFrame frame1 = JFrameOperator.findJFrame(PredicatesJ.byName("JFrameOperatorTest"));
        assertNotNull(frame1);
        JFrame frame2 = JFrameOperator.findJFrame("JFrameOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(frame2);
    }

    @Test
    void waitJFrame() throws Exception {
        jFrameRef.get().setVisible(true);
        JFrame frame1 = JFrameOperator.waitJFrame(PredicatesJ.byName("JFrameOperatorTest"));
        assertNotNull(frame1);
        JFrame frame2 = JFrameOperator.waitJFrame("JFrameOperatorTest");
        assertNotNull(frame2);
        WaitJFrameCallable callable = new WaitJFrameCallable();
        Future<JFrame> future = Executors.newSingleThreadExecutor().submit(callable);
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);

        try {
            assertNull(future.get(1000, TimeUnit.MILLISECONDS));
            fail("did not work");
        } catch (TimeoutException e) {
            assertTrue(true);
        }
    }

    @Test
    void getAccessibleContext() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        assertNotNull(operator.getAccessibleContext());
    }

    @Test
    void getContentPane() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JScrollPane scrollPane = new JScrollPane();
        operator.setContentPane(scrollPane);
        assertEquals(jFrameRef.get().getContentPane(), scrollPane);
        assertNotNull(operator.getContentPane());
    }

    @Test
    void getDefaultCloseOperation() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        operator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        assertEquals(jFrameRef.get().getDefaultCloseOperation(), JFrame.EXIT_ON_CLOSE);
        assertEquals(operator.getDefaultCloseOperation(), JFrame.EXIT_ON_CLOSE);
    }

    @Test
    void getGlassPane() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JScrollPane scrollPane = new JScrollPane();
        operator.setGlassPane(scrollPane);
        assertEquals(jFrameRef.get().getGlassPane(), scrollPane);
        assertNotNull(operator.getGlassPane());
    }

    @Test
    void getJMenuBar() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JMenuBar menuBar = new JMenuBar();
        operator.setJMenuBar(menuBar);
        assertEquals(jFrameRef.get().getJMenuBar(), menuBar);
        assertEquals(operator.getJMenuBar(), jFrameRef.get().getJMenuBar());
    }

    @Test
    void getLayeredPane() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JLayeredPane layeredPane = new JLayeredPane();
        operator.setLayeredPane(layeredPane);
        assertEquals(jFrameRef.get().getLayeredPane(), layeredPane);
        assertNotNull(operator.getLayeredPane());
    }

    @Test
    void getRootPane() {
        jFrameRef.get().setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        assertEquals(operator.getRootPane(), jFrameRef.get().getRootPane());
    }

    // formerly scenario test jemmy_009
    @Test
    void waitJFrameByIndexAndRetitle() throws Exception {
        IndexedFramesApp.main(new String[] {});
        QueueTool.getInstance().waitEmpty();
        try {
            JFrame frm0 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring());
            assertEquals(0, ((IndexedFramesApp) frm0).getIndex());
            JFrame frm1 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring(), 1);
            assertEquals(1, ((IndexedFramesApp) frm1).getIndex());
            JFrame frm2 = JFrameOperator.waitJFrame("IndexedFramesApp", StringComparators.substring(), 2);
            assertEquals(2, ((IndexedFramesApp) frm2).getIndex());
            JFrameOperator frm2o = new JFrameOperator(frm2);
            frm2o.setTitle("New Title");
            frm2o.waitTitle("New Title", StringComparators.strict());
        } finally {
            disposeApplicationFrames();
        }
    }

    // formerly scenario test jemmy_038
    @Test
    void frameLifecycleSequence() throws Exception {
        MenuNavigationApp.main(new String[] {});
        try {
            JFrame win = JFrameOperator.waitJFrame("MenuNavigationApp");
            JFrameOperator fo = new JFrameOperator(win);
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

    private void disposeApplicationFrames() throws Exception {
        EventQueue.invokeAndWait(() -> {
            for (Frame frame : Frame.getFrames()) {
                if (frame != jFrameRef.get()) {
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
