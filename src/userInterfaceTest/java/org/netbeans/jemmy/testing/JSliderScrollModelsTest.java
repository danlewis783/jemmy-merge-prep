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

import java.util.Objects;
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
        JFrameOperator wino = new JFrameOperator(win);
        JSliderOperator[] ops = {
            new JSliderOperator(Objects.requireNonNull(JSliderOperator.findJSlider(win, 0))),
            new JSliderOperator(Objects.requireNonNull(JSliderOperator.findJSlider(win, 1))),
            new JSliderOperator(Objects.requireNonNull(JSliderOperator.findJSlider(win, 2))),
            new JSliderOperator(Objects.requireNonNull(JSliderOperator.findJSlider(win, 3)))
        };
        for (int i = 0; i < ops.length; i++) {
            assertThat(new JSliderOperator(wino, i).getSource()).isSameAs(ops[i].getSource());
        }

        JLabel label = Objects.requireNonNull(JLabelOperator.findJLabel(win, "0", StringComparators.strict()));
        int value;
        for (JSliderOperator op : ops) {
            int min = op.getMinimum();
            int max = op.getMaximum();
            int range = max - min;
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToMaximum();
            assertThat(Integer.parseInt(label.getText())).isEqualTo(max);
            value = div10(2.0 * range / 3.0);
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToValue(value);
            assertThat(Integer.parseInt(label.getText())).isEqualTo(value);
            value = div10((double) range / 3);
            op.setScrollModel(JSliderOperator.PUSH_AND_WAIT_SCROLL_MODEL);
            op.scrollToValue(value);
            assertThat(Integer.parseInt(label.getText())).isEqualTo(value);
            op.setScrollModel(JSliderOperator.CLICK_SCROLL_MODEL);
            op.scrollToMinimum();
            assertThat(Integer.parseInt(label.getText())).isEqualTo(min);
        }

        JSliderOperator op = ops[0];
        JSlider src = (JSlider) op.getSource();
        assertThat(op.getExtent()).isEqualTo(src.getExtent());
        assertThat(op.getInverted()).isEqualTo(src.getInverted());
        assertThat(op.getLabelTable()).isEqualTo(src.getLabelTable());
        assertThat(op.getMajorTickSpacing()).isEqualTo(src.getMajorTickSpacing());
        assertThat(op.getMaximum()).isEqualTo(src.getMaximum());
        assertThat(op.getMinimum()).isEqualTo(src.getMinimum());
        assertThat(op.getMinorTickSpacing()).isEqualTo(src.getMinorTickSpacing());
        assertThat(op.getModel()).isEqualTo(src.getModel());
        assertThat(op.getOrientation()).isEqualTo(src.getOrientation());
        assertThat(op.getPaintLabels()).isEqualTo(src.getPaintLabels());
        assertThat(op.getPaintTicks()).isEqualTo(src.getPaintTicks());
        assertThat(op.getPaintTrack()).isEqualTo(src.getPaintTrack());
        assertThat(op.getSnapToTicks()).isEqualTo(src.getSnapToTicks());
        assertThat(op.getUI()).isEqualTo(src.getUI());
        assertThat(op.getValue()).isEqualTo(src.getValue());
        assertThat(op.getValueIsAdjusting()).isEqualTo(src.getValueIsAdjusting());
    }

    private int div10(double range) {
        int iRange = (int) range;
        return iRange - iRange % 10;
    }
}
