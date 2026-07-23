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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

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
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.testing.TestWindows;
import org.netbeans.jemmy.util.StringComparators;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JFrameOperatorTest {
    private JFrame mainFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame = new JFrame("JFrameOperatorTest");
            mainFrame.setName("JFrameOperatorTest");
            mainFrame.pack();
            TestWindows.place(mainFrame);
            mainFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainFrame.setVisible(false);
            mainFrame.dispose();
        });
    }

    @Test
    void constructor() throws InterruptedException, InvocationTargetException {

        JFrameOperator.waitFor();
        JFrameOperator.waitFor("JFrameOperatorTest");
        JFrameOperator.waitFor(PredicatesJ.byName("JFrameOperatorTest"));
    }

    @Test
    void findJFrame() throws InterruptedException, InvocationTargetException {
        JFrame frame1 = JFrameOperator.findJFrame(PredicatesJ.byName("JFrameOperatorTest"));
        assertThat(frame1).isNotNull();
        JFrame frame2 = JFrameOperator.findJFrame("JFrameOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(frame2).isNotNull();
    }

    @Test
    @Timeout(value=2, unit=TimeUnit.SECONDS)
    void waitJFrame() throws InterruptedException, InvocationTargetException {
        JFrame frameOpByName = JFrameOperator.waitJFrame(PredicatesJ.byName("JFrameOperatorTest"));
        JFrame frameOpByTitle = JFrameOperator.waitJFrame("JFrameOperatorTest");

        Future<JFrame> laFutura = Executors.newSingleThreadExecutor().submit(new WaitJFrameCallable());
        JFrameOperator.waitFor();

        assertThatExceptionOfType(TimeoutException.class).isThrownBy(() -> laFutura.get(1_000, TimeUnit.MILLISECONDS));
    }

    @Test
    void getAccessibleContext() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator.getAccessibleContext()).isNotNull();
    }

    @Test
    void getContentPane() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        JScrollPane scrollPane = onQueue(JScrollPane::new);
        operator.setContentPane(scrollPane);
        assertThat(onQueue(mainFrame::getContentPane)).isEqualTo(scrollPane);
        assertThat(operator.getContentPane()).isNotNull();
    }

    @Test
    void getDefaultCloseOperation() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        operator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        assertThat(onQueue(mainFrame::getDefaultCloseOperation)).isEqualTo(JFrame.EXIT_ON_CLOSE);
        assertThat(operator.getDefaultCloseOperation()).isEqualTo(JFrame.EXIT_ON_CLOSE);
    }

    @Test
    void getGlassPane() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        JScrollPane scrollPane = onQueue(JScrollPane::new);
        operator.setGlassPane(scrollPane);
        assertThat(onQueue(mainFrame::getGlassPane)).isEqualTo(scrollPane);
        assertThat(operator.getGlassPane()).isNotNull();
    }

    @Test
    void getJMenuBar() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        JMenuBar menuBar = onQueue(JMenuBar::new);
        operator.setJMenuBar(menuBar);
        assertThat(onQueue(mainFrame::getJMenuBar)).isEqualTo(menuBar);
        assertThat(operator.getJMenuBar()).isEqualTo(menuBar);
    }

    @Test
    void getLayeredPane() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        JLayeredPane layeredPane = onQueue(JLayeredPane::new);
        operator.setLayeredPane(layeredPane);
        assertThat(onQueue(mainFrame::getLayeredPane)).isEqualTo(layeredPane);
        assertThat(operator.getLayeredPane()).isNotNull();
    }

    @Test
    void getRootPane() throws InterruptedException, InvocationTargetException {
        JFrameOperator operator = JFrameOperator.waitFor();
        assertThat(operator.getRootPane()).isEqualTo(onQueue(mainFrame::getRootPane));
    }

    private static class WaitJFrameCallable implements Callable<JFrame> {
        @Override
        public JFrame call() {
            return JFrameOperator.waitJFrame("YouWontEverFindMe");
        }
    }
}
