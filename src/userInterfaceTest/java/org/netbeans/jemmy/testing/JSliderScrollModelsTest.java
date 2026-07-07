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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_025
class JSliderScrollModelsTest {

    @Test
    void test() {
        SlidersApp.main(new String[] {});
        JFrame win = JFrameOperator.waitJFrame("SlidersApp");
        JFrameOperator wino = new JFrameOperator(win);
        JSliderOperator[] ops = {
            new JSliderOperator(JSliderOperator.findJSlider(win, 0)),
            new JSliderOperator(JSliderOperator.findJSlider(win, 1)),
            new JSliderOperator(JSliderOperator.findJSlider(win, 2)),
            new JSliderOperator(JSliderOperator.findJSlider(win, 3))
        };
        for (int i = 0; i < ops.length; i++) {
            assertSame(ops[i].getSource(), new JSliderOperator(wino, i).getSource());
        }

        JLabel label = JLabelOperator.findJLabel(win, "0", StringComparators.strict());
        int value;
        for (JSliderOperator op : ops) {
            int min = op.getMinimum();
            int max = op.getMaximum();
            int range = max - min;
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToMaximum();
            assertEquals(max, Integer.parseInt(label.getText()));
            value = div10(2.0 * range / 3.0);
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToValue(value);
            assertEquals(value, Integer.parseInt(label.getText()));
            value = div10(range / 3);
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToValue(value);
            assertEquals(value, Integer.parseInt(label.getText()));
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToMinimum();
            assertEquals(min, Integer.parseInt(label.getText()));
        }

        JSliderOperator op = ops[0];
        JSlider src = (JSlider) op.getSource();
        assertEquals(src.getExtent(), op.getExtent());
        assertEquals(src.getInverted(), op.getInverted());
        assertEquals(src.getLabelTable(), op.getLabelTable());
        assertEquals(src.getMajorTickSpacing(), op.getMajorTickSpacing());
        assertEquals(src.getMaximum(), op.getMaximum());
        assertEquals(src.getMinimum(), op.getMinimum());
        assertEquals(src.getMinorTickSpacing(), op.getMinorTickSpacing());
        assertEquals(src.getModel(), op.getModel());
        assertEquals(src.getOrientation(), op.getOrientation());
        assertEquals(src.getPaintLabels(), op.getPaintLabels());
        assertEquals(src.getPaintTicks(), op.getPaintTicks());
        assertEquals(src.getPaintTrack(), op.getPaintTrack());
        assertEquals(src.getSnapToTicks(), op.getSnapToTicks());
        assertEquals(src.getUI(), op.getUI());
        assertEquals(src.getValue(), op.getValue());
        assertEquals(src.getValueIsAdjusting(), op.getValueIsAdjusting());
    }

    private int div10(double range) {
        int iRange = (int) range;
        return iRange - iRange % 10;
    }
}
