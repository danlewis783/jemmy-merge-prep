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

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

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
        SlidersApp.main();
        JFrame win = JFrameOperator.waitJFrame("SlidersApp");
        JFrameOperator wino = JFrameOperator.of(win);
        JSlider slider0 = JSliderOperator.findJSlider(win, 0);
        assertThat(slider0).isNotNull();
        JSlider slider1 = JSliderOperator.findJSlider(win, 1);
        assertThat(slider1).isNotNull();
        JSlider slider2 = JSliderOperator.findJSlider(win, 2);
        assertThat(slider2).isNotNull();
        JSlider slider3 = JSliderOperator.findJSlider(win, 3);
        assertThat(slider3).isNotNull();
        JSliderOperator[] ops = {
            JSliderOperator.of(slider0),
            JSliderOperator.of(slider1),
            JSliderOperator.of(slider2),
            JSliderOperator.of(slider3)
        };
        for (int i = 0; i < ops.length; i++) {
            assertThat(JSliderOperator.waitFor(wino, i).getSource()).isSameAs(ops[i].getSource());
        }

        JLabel label = JLabelOperator.findJLabel(win, "0", StringComparators.strict());
        assertThat(label).isNotNull();
        int value;
        for (JSliderOperator op : ops) {
            int min = op.getMinimum();
            int max = op.getMaximum();
            int range = max - min;
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToMaximum();
            assertThat(Integer.parseInt(onQueue(label::getText))).isEqualTo(max);
            value = div10(2.0 * range / 3.0);
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToValue(value);
            assertThat(Integer.parseInt(onQueue(label::getText))).isEqualTo(value);
            value = div10((double) range / 3);
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToValue(value);
            assertThat(Integer.parseInt(onQueue(label::getText))).isEqualTo(value);
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToMinimum();
            assertThat(Integer.parseInt(onQueue(label::getText))).isEqualTo(min);
        }

        JSliderOperator op = ops[0];
        JSlider src = (JSlider) op.getSource();
        assertThat(op.getExtent()).isEqualTo(onQueue(src::getExtent));
        assertThat(op.getInverted()).isEqualTo(onQueue(src::getInverted));
        assertThat(op.getLabelTable()).isEqualTo(onQueue(src::getLabelTable));
        assertThat(op.getMajorTickSpacing()).isEqualTo(onQueue(src::getMajorTickSpacing));
        assertThat(op.getMaximum()).isEqualTo(onQueue(src::getMaximum));
        assertThat(op.getMinimum()).isEqualTo(onQueue(src::getMinimum));
        assertThat(op.getMinorTickSpacing()).isEqualTo(onQueue(src::getMinorTickSpacing));
        assertThat(op.getModel()).isEqualTo(onQueue(src::getModel));
        assertThat(op.getOrientation()).isEqualTo(onQueue(src::getOrientation));
        assertThat(op.getPaintLabels()).isEqualTo(onQueue(src::getPaintLabels));
        assertThat(op.getPaintTicks()).isEqualTo(onQueue(src::getPaintTicks));
        assertThat(op.getPaintTrack()).isEqualTo(onQueue(src::getPaintTrack));
        assertThat(op.getSnapToTicks()).isEqualTo(onQueue(src::getSnapToTicks));
        assertThat(op.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(op.getValue()).isEqualTo(onQueue(src::getValue));
        assertThat(op.getValueIsAdjusting()).isEqualTo(onQueue(src::getValueIsAdjusting));
    }

    private int div10(double range) {
        int iRange = (int) range;
        return iRange - iRange % 10;
    }
}
