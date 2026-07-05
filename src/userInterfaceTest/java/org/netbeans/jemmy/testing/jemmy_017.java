package org.netbeans.jemmy.testing;

import java.util.ArrayList;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeAll;
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

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class jemmy_017 {




    @Test
    void doit() {
        ComponentOperator.setDefaultComponentVisualizer(new MouseVisualizer(.5, 5));
        TimeoutOverride override = Timeouts.override(TimeoutKey.ComponentOperator_WaitComponentTimeout, 20000L);
        try {
            Application_017.main(new String[]{});
            WindowManager.addJob(new WindowProcessor());
            JFrame jFrame;
            assertNotNull(jFrame = JFrameOperator.waitJFrame("Application_017/0", StringComparators.substring()));
            assertNotNull(JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict()));
            assertNotNull(jFrame = JFrameOperator.waitJFrame("Application_017/1", StringComparators.substring()));
            assertNotNull(JLabelOperator.waitJLabel(jFrame, "has been processed", StringComparators.strict()));
            assertNotNull(jFrame = JFrameOperator.waitJFrame("Application_017/2", StringComparators.substring()));
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
                if (! (input instanceof JFrame)) {
                    return false;
                }
                JFrame jFrame = (JFrame) input;
                return !processed.contains(jFrame) && jFrame.getTitle().startsWith("Application_017");
            };
        }

        @Override
        public Void apply(JFrame jFrame) {
            try {
                new JButtonOperator(JButtonOperator.waitJButton(jFrame, "process", StringComparators.substring())).push();
                processed.add(jFrame);
            } catch (TimeoutExpiredException e) {
                // don't care
            }

            return null;
        }
    }
}
