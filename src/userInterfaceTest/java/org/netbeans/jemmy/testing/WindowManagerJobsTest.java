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

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.util.MouseVisualizer;
import org.netbeans.jemmy.util.StringComparators;
import org.netbeans.jemmy.util.WindowFunction;
import org.netbeans.jemmy.util.WindowManager;

// formerly scenario test jemmy_017
// 20s rather than 5s: the MouseVisualizer's real robot click pays the one-time
// robot-coordinate calibration (a few seconds of probing) on scaled displays
@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=20, unit=TimeUnit.SECONDS)
class WindowManagerJobsTest {

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            new WindowSeriesFrame(0).setVisible(true);
            new WindowSeriesFrame(1).setVisible(true);
            new WindowSeriesFrame(2).setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        // jobs poll in background threads until stopped; a leaked one would keep clicking
        // matching windows in later tests
        WindowManager.removeAllJobs();
        TestWindows.disposeAll();
    }

    @Test
    void doit() {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 20_000L)) {
            WindowManager.addJob(new WindowProcessor());
            JFrame jFrame;
            jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/0", StringComparators.substring());
            JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict());
            jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/1", StringComparators.substring());
            JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict());
            jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/2", StringComparators.substring());
            JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict());
        }
    }

    @Test
    void removeAllJobsStopsAJobItsCreatorNeverRemoved() throws Exception {
        try (TimeoutOverride override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 20_000L)) {
            WindowManager.addJob(new WindowProcessor());
            JFrame jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/0", StringComparators.substring());
            JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict());

            WindowManager.removeAllJobs();

            EventQueue.invokeAndWait(() -> new WindowSeriesFrame(3).setVisible(true));
            // three polling periods: ample opportunity for a still-running job to process it
            Thread.sleep(3 * Timeouts.get(TimeoutKey.WindowManager_TimeDelta));
            JFrame lateFrame = JFrameOperator.waitJFrame("WindowSeriesApp/3", StringComparators.substring());
            JLabelOperator.waitJLabel(lateFrame, "has not been processed", StringComparators.strict());
        }
    }

    private static class WindowSeriesFrame extends JFrame {

        private WindowSeriesFrame(int index) {
            super("WindowSeriesApp/" + index);
            setSize(300, 300);
            TestWindows.place(this, index);
            getContentPane().setLayout(new FlowLayout());
            JLabel label = new JLabel("has not been processed");
            getContentPane().add(label);
            getContentPane().add(new JButton("another button"));
            JButton close = new JButton("process " + index);
            close.addActionListener(e -> label.setText("has been processed"));
            getContentPane().add(close);
        }
    }

    private static class WindowProcessor implements WindowFunction<JFrame> {
        /** For diagnosing timeouts: relates processing progress to the test's 20s budget. */
        private static final long START = System.currentTimeMillis();

        private final List<JFrame> processed;

        private WindowProcessor() {
            processed = new ArrayList<>();
        }

        @Override
        public Predicate<Component> getPredicate() {
            return input -> {
                if (!(input instanceof JFrame)) {
                    return false;
                }
                JFrame jFrame = (JFrame) input;
                return !processed.contains(jFrame) && jFrame.getTitle().startsWith("WindowSeriesApp");
            };
        }

        @Override
        public Void apply(JFrame jFrame) {
            try {
                System.out.println("processing " + jFrame.getTitle() + " at " + (System.currentTimeMillis() - START) + " ms");
                JButtonOperator buttonOp =
                        JButtonOperator.of(JButtonOperator.waitJButton(jFrame, "process", StringComparators.substring()));
                buttonOp.setVisualizer(new MouseVisualizer(.5, 5));
                buttonOp.push();
                System.out.println("processed " + jFrame.getTitle() + " at " + (System.currentTimeMillis() - START) + " ms");
                processed.add(jFrame);
            } catch (TimeoutExpiredException e) {
                // tolerated, but never silently: a swallowed failure here surfaces later as an
                // unexplained waitJLabel timeout in the test body
                e.printStackTrace();
            }

            return null;
        }
    }
}
