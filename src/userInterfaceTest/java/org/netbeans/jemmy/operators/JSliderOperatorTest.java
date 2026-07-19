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
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.drivers.scrolling.JSliderAPIDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.testing.TestWindows;

@Timeout(value=1, unit=TimeUnit.SECONDS)
class JSliderOperatorTest {

    private JFrame frame;
    private JSlider slider;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            slider = new JSlider();
            slider.setName("JSliderOperatorTest");
            frame.getContentPane().add(slider);
            frame.pack();
            TestWindows.place(frame);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator.waitFor(operator);
        JSliderOperator.waitFor(operator, ComponentPredicates.byName("JSliderOperatorTest"));
    }

    @Test
    void testFindJSlider() {
        JSlider slider1 = JSliderOperator.findJSlider(frame);
        assertThat(slider1).isNotNull();
        JSlider slider2 = JSliderOperator.findJSlider(frame, ComponentPredicates.byName("JSliderOperatorTest"));
        assertThat(slider2).isNotNull();
    }

    @Test
    void testWaitJSlider() {
        JSliderOperator.waitJSlider(frame);
        JSliderOperator.waitJSlider(frame, ComponentPredicates.byName("JSliderOperatorTest"));
    }

    @Test
    void testSetScrollModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
        assertThat(operator2.getScrollModel()).isEqualTo(JSliderOperator.CLICK_SCROLL_MODEL);
    }

    @Test
    void testScrollTo() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        ScrollAdjusterTest adjuster = new ScrollAdjusterTest();
        operator2.scrollTo(adjuster);
    }

    @Test
    void testScrollToValue() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.scrollToValue(10);
        assertThat(operator2.getValue()).isEqualTo(10);
    }

    @Test
    void testApiDriverScrollsToNegativeValue() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            slider.setMinimum(-10);
            slider.setMaximum(10);
            slider.setValue(0);
        });
        JSliderOperator operator = JSliderOperator.of(slider);
        JSliderAPIDriver driver = new JSliderAPIDriver();
        driver.scroll(operator, new ValueTargetAdjuster(operator, -1));
        assertThat(operator.getValue()).isEqualTo(-1);
    }

    @Test
    void testScrollToMaximum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMinimum(0);
        operator2.setMaximum(100);
        operator2.setValue(100);
        operator2.scrollToMaximum();
    }

    @Test
    void testScrollToMinimum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.scrollToMinimum();
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        IgnoreChangeListener listener = new IgnoreChangeListener();
        operator2.addChangeListener(listener);
        assertThat(onQueue(slider::getChangeListeners)).hasSize(1);
        operator2.removeChangeListener(listener);
        assertThat(onQueue(slider::getChangeListeners)).isEmpty();
    }

    @Test
    void testCreateStandardLabels() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.createStandardLabels(10, 10);
        operator2.createStandardLabels(10);
    }

    @Test
    void testGetExtent() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setExtent(10);
        assertThat(operator2.getExtent()).isEqualTo(10);
    }

    @Test
    void testGetInverted() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setInverted(true);
        assertThat(operator2.getInverted()).isTrue();
        operator2.setInverted(false);
        assertThat(operator2.getInverted()).isFalse();
    }

    @Test
    void testGetLabelTable() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        Dictionary<String, String> hashtable = new Hashtable<>();
        operator2.setLabelTable(hashtable);
        assertThat(operator2.getLabelTable()).isEqualTo(hashtable);
    }

    @Test
    void testGetMajorTickSpacing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMajorTickSpacing(11);
        assertThat(operator2.getMajorTickSpacing()).isEqualTo(11);
        assertThat(onQueue(slider::getMajorTickSpacing)).isEqualTo(11);
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMaximum(111);
        assertThat(operator2.getMaximum()).isEqualTo(111);
        assertThat(onQueue(slider::getMaximum)).isEqualTo(111);
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMinimum(11);
        assertThat(operator2.getMinimum()).isEqualTo(11);
        assertThat(onQueue(slider::getMinimum)).isEqualTo(11);
    }

    @Test
    void testGetMinorTickSpacing() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMinorTickSpacing(7);
        assertThat(operator2.getMinorTickSpacing()).isEqualTo(7);
        assertThat(onQueue(slider::getMinorTickSpacing)).isEqualTo(7);
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        BoundedRangeModelTest model = new BoundedRangeModelTest();
        operator2.setModel(model);
        assertThat(operator2.getModel()).isEqualTo(model);
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setOrientation(SwingConstants.VERTICAL);
        assertThat(operator2.getOrientation()).isEqualTo(SwingConstants.VERTICAL);
    }

    @Test
    void testGetPaintLabels() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setPaintLabels(true);
        assertThat(operator2.getPaintLabels()).isTrue();
        operator2.setPaintLabels(false);
        assertThat(operator2.getPaintLabels()).isFalse();
    }

    @Test
    void testGetPaintTicks() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setPaintTicks(true);
        assertThat(operator2.getPaintTicks()).isTrue();
        operator2.setPaintTicks(false);
        assertThat(operator2.getPaintTicks()).isFalse();
    }

    @Test
    void testGetPaintTrack() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setPaintTrack(true);
        assertThat(operator2.getPaintTrack()).isTrue();
        operator2.setPaintTrack(false);
        assertThat(operator2.getPaintTrack()).isFalse();
    }

    @Test
    void testGetSnapToTicks() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setSnapToTicks(true);
        assertThat(operator2.getSnapToTicks()).isTrue();
        operator2.setSnapToTicks(false);
        assertThat(operator2.getSnapToTicks()).isFalse();
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        NoopSliderUI sliderUI = new NoopSliderUI();
        operator2.setUI(sliderUI);
        assertThat(operator2.getUI()).isEqualTo(sliderUI);
    }

    @Test
    void testGetValueIsAdjusting() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setValueIsAdjusting(true);
        assertThat(operator2.getValueIsAdjusting()).isTrue();
        operator2.setValueIsAdjusting(false);
        assertThat(operator2.getValueIsAdjusting()).isFalse();
    }

    @Test
    void testSetMaximum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMaximum(100);
        assertThat(operator2.getMaximum()).isEqualTo(100);
    }

    @Test
    void testSetMinimum() {
        JFrameOperator operator = JFrameOperator.waitFor();
        JSliderOperator operator2 = JSliderOperator.waitFor(operator);
        operator2.setMinimum(100);
        assertThat(operator2.getMinimum()).isEqualTo(100);
    }

    private static class BoundedRangeModelTest implements BoundedRangeModel {
        @Override
        public int getMinimum() {
            return -1;
        }

        @Override
        public void setMinimum(int newMinimum) {}

        @Override
        public int getMaximum() {
            return -1;
        }

        @Override
        public void setMaximum(int newMaximum) {}

        @Override
        public int getValue() {
            return -1;
        }

        @Override
        public void setValue(int newValue) {}

        @Override
        public void setValueIsAdjusting(boolean b) {}

        @Override
        public boolean getValueIsAdjusting() {
            return false;
        }

        @Override
        public int getExtent() {
            return -1;
        }

        @Override
        public void setExtent(int newExtent) {}

        @Override
        public void setRangeProperties(int value, int extent, int min, int max, boolean adjusting) {}

        @Override
        public void addChangeListener(ChangeListener x) {}

        @Override
        public void removeChangeListener(ChangeListener x) {}
    }

    private static class IgnoreChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private static class NoopSliderUI extends SliderUI {}

    static class ScrollAdjusterTest implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return 0;
        }
    }

    private static class ValueTargetAdjuster implements ScrollAdjuster {
        private final JSliderOperator operator;
        private final int target;
        // gives up instead of scrolling forever, so a regressed driver fails the value
        // assertion rather than hanging the test
        private final long deadline = System.currentTimeMillis() + 15_000;

        ValueTargetAdjuster(JSliderOperator operator, int target) {
            this.operator = operator;
            this.target = target;
        }

        @Override
        public int getScrollDirection() {
            if (System.currentTimeMillis() > deadline) {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            }

            int value = operator.getValue();
            if (value > target) {
                return DECREASE_SCROLL_DIRECTION;
            } else if (value < target) {
                return INCREASE_SCROLL_DIRECTION;
            } else {
                return DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.HORIZONTAL;
        }
    }
}
