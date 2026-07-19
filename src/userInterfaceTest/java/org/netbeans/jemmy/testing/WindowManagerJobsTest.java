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
@Timeout(value=5, unit=TimeUnit.SECONDS)
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
                JButtonOperator buttonOp =
                        JButtonOperator.of(JButtonOperator.waitJButton(jFrame, "process", StringComparators.substring()));
                buttonOp.setVisualizer(new MouseVisualizer(.5, 5));
                buttonOp.push();
                processed.add(jFrame);
            } catch (TimeoutExpiredException e) {
                // don't care
            }

            return null;
        }
    }
}
