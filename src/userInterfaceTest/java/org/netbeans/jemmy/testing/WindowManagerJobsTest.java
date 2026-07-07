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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.util.MouseVisualizer;
import org.netbeans.jemmy.util.StringComparators;
import org.netbeans.jemmy.util.WindowFunction;
import org.netbeans.jemmy.util.WindowManager;

// formerly scenario test jemmy_017
class WindowManagerJobsTest {

    @Test
    void doit() {
        ComponentOperator.setDefaultComponentVisualizer(new MouseVisualizer(.5, 5));
        TimeoutOverride override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 20000L);
        try {
            WindowSeriesApp.main(new String[] {});
            WindowManager.addJob(new WindowProcessor());
            JFrame jFrame;
            assertNotNull(jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/0", StringComparators.substring()));
            assertNotNull(JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict()));
            assertNotNull(jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/1", StringComparators.substring()));
            assertNotNull(JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict()));
            assertNotNull(jFrame = JFrameOperator.waitJFrame("WindowSeriesApp/2", StringComparators.substring()));
            assertNotNull(JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict()));
        } finally {
            override.cancel();
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
                new JButtonOperator(JButtonOperator.waitJButton(jFrame, "process", StringComparators.substring()))
                        .push();
                processed.add(jFrame);
            } catch (TimeoutExpiredException e) {
                // don't care
            }

            return null;
        }
    }
}
