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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Dictionary;
import java.util.Hashtable;
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
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;

class JSliderOperatorTest {

    private JFrame frame;
    private JSlider slider;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new JFrame();
                slider = new JSlider();
                slider.setName("JSliderOperatorTest");
                frame.getContentPane().add(slider);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        JSliderOperator operator3 = new JSliderOperator(operator, PredicatesJ.byName("JSliderOperatorTest"));
        assertNotNull(operator3);
    }

    @Test
    void testFindJSlider() {
        JSlider slider1 = JSliderOperator.findJSlider(frame);
        assertNotNull(slider1);
        JSlider slider2 = JSliderOperator.findJSlider(frame, PredicatesJ.byName("JSliderOperatorTest"));
        assertNotNull(slider2);
    }

    @Test
    void testWaitJSlider() {
        JSlider slider1 = JSliderOperator.waitJSlider(frame);
        assertNotNull(slider1);
        JSlider slider2 = JSliderOperator.waitJSlider(frame, PredicatesJ.byName("JSliderOperatorTest"));
        assertNotNull(slider2);
    }

    @Test
    void testSetScrollModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
        assertEquals(JSliderOperator.CLICK_SCROLL_MODEL, operator2.getScrollModel());
    }

    @Test
    void testScrollTo() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        ScrollAdjusterTest adjuster = new ScrollAdjusterTest();
        operator2.scrollTo(adjuster);
    }

    @Test
    void testScrollToValue() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.scrollToValue(10);
        assertEquals(10, operator2.getValue());
    }

    @Test
    void testScrollToMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMinimum(0);
        operator2.setMaximum(100);
        operator2.setValue(100);
        operator2.scrollToMaximum();
    }

    @Test
    void testScrollToMinimum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.scrollToMinimum();
    }

    @Test
    void testAddChangeListener() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        IgnoreChangeListener listener = new IgnoreChangeListener();
        operator2.addChangeListener(listener);
        assertEquals(1, slider.getChangeListeners().length);
        operator2.removeChangeListener(listener);
        assertEquals(0, slider.getChangeListeners().length);
    }

    @Test
    void testCreateStandardLabels() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.createStandardLabels(10, 10);
        operator2.createStandardLabels(10);
    }

    @Test
    void testGetExtent() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setExtent(10);
        assertEquals(10, operator2.getExtent());
    }

    @Test
    void testGetInverted() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setInverted(true);
        assertTrue(operator2.getInverted());
        operator2.setInverted(false);
        assertTrue(!operator2.getInverted());
    }

    @Test
    void testGetLabelTable() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        Dictionary<String, String> hashtable = new Hashtable<>();
        operator2.setLabelTable(hashtable);
        assertEquals(hashtable, operator2.getLabelTable());
    }

    @Test
    void testGetMajorTickSpacing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMajorTickSpacing(11);
        assertEquals(11, operator2.getMajorTickSpacing());
        assertEquals(11, slider.getMajorTickSpacing());
    }

    @Test
    void testGetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMaximum(111);
        assertEquals(111, operator2.getMaximum());
        assertEquals(111, slider.getMaximum());
    }

    @Test
    void testGetMinimum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMinimum(11);
        assertEquals(11, operator2.getMinimum());
        assertEquals(11, slider.getMinimum());
    }

    @Test
    void testGetMinorTickSpacing() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMinorTickSpacing(7);
        assertEquals(7, operator2.getMinorTickSpacing());
        assertEquals(7, slider.getMinorTickSpacing());
    }

    @Test
    void testGetModel() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        BoundedRangeModelTest model = new BoundedRangeModelTest();
        operator2.setModel(model);
        assertEquals(model, operator2.getModel());
    }

    @Test
    void testGetOrientation() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setOrientation(SwingConstants.VERTICAL);
        assertEquals(SwingConstants.VERTICAL, operator2.getOrientation());
    }

    @Test
    void testGetPaintLabels() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setPaintLabels(true);
        assertTrue(operator2.getPaintLabels());
        operator2.setPaintLabels(false);
        assertTrue(!operator2.getPaintLabels());
    }

    @Test
    void testGetPaintTicks() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setPaintTicks(true);
        assertTrue(operator2.getPaintTicks());
        operator2.setPaintTicks(false);
        assertTrue(!operator2.getPaintTicks());
    }

    @Test
    void testGetPaintTrack() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setPaintTrack(true);
        assertTrue(operator2.getPaintTrack());
        operator2.setPaintTrack(false);
        assertTrue(!operator2.getPaintTrack());
    }

    @Test
    void testGetSnapToTicks() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setSnapToTicks(true);
        assertTrue(operator2.getSnapToTicks());
        operator2.setSnapToTicks(false);
        assertTrue(!operator2.getSnapToTicks());
    }

    @Test
    void testGetUI() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        NoopSliderUI sliderUI = new NoopSliderUI();
        operator2.setUI(sliderUI);
        assertEquals(sliderUI, operator2.getUI());
    }

    @Test
    void testGetValueIsAdjusting() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setValueIsAdjusting(true);
        assertTrue(operator2.getValueIsAdjusting());
        operator2.setValueIsAdjusting(false);
        assertTrue(!operator2.getValueIsAdjusting());
    }

    @Test
    void testSetMaximum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMaximum(100);
        assertEquals(100, operator2.getMaximum());
    }

    @Test
    void testSetMinimum() {
        JFrameOperator operator = new JFrameOperator();
        assertNotNull(operator);
        JSliderOperator operator2 = new JSliderOperator(operator);
        assertNotNull(operator2);
        operator2.setMinimum(100);
        assertEquals(100, operator2.getMinimum());
    }

    private class BoundedRangeModelTest implements BoundedRangeModel {
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

    private class IgnoreChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {}
    }

    private class NoopSliderUI extends SliderUI {}

    class ScrollAdjusterTest implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return 0;
        }
    }
}
