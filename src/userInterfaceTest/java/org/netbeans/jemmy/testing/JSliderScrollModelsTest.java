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

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_025
@Timeout(value=10, unit=TimeUnit.SECONDS)
class JSliderScrollModelsTest {
    private static final String FRAME_TITLE = "JSliderScrollModelsTest";
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame(FRAME_TITLE);
            this.jFrame = jFrame;

            JLabel label = new JLabel("0");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            JSlider hSlider = new JSlider(JSlider.HORIZONTAL);
            hSlider.addChangeListener(e -> label.setText(String.valueOf(hSlider.getValue())));
            JSlider hQSlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 0);
            hQSlider.addChangeListener(e -> label.setText(String.valueOf(hQSlider.getValue())));
            hQSlider.setInverted(true);
            hQSlider.setPaintLabels(true);
            hQSlider.setPaintTicks(true);
            hQSlider.setPaintTrack(true);
            JSlider vSlider = new JSlider(JSlider.VERTICAL);
            vSlider.addChangeListener(e -> label.setText(String.valueOf(vSlider.getValue())));
            JSlider vQSlider = new JSlider(JSlider.VERTICAL, 0, 3, 0);
            vQSlider.addChangeListener(e -> label.setText(String.valueOf(vQSlider.getValue())));
            vQSlider.setInverted(true);
            vQSlider.setPaintLabels(true);
            vQSlider.setPaintTicks(true);
            vQSlider.setPaintTrack(true);
            JPanel pane = new JPanel();
            pane.setLayout(new BorderLayout());
            pane.add(hSlider, BorderLayout.SOUTH);
            pane.add(hQSlider, BorderLayout.NORTH);
            pane.add(vSlider, BorderLayout.EAST);
            pane.add(vQSlider, BorderLayout.WEST);
            pane.add(label, BorderLayout.CENTER);
            jFrame.getContentPane().setLayout(new BorderLayout());
            jFrame.getContentPane().add(pane, BorderLayout.CENTER);
            jFrame.setSize(400, 400);
            jFrame.setVisible(true);
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        });
    }

    @Test
    void test() {
        JFrameOperator jFrameOp = JFrameOperator.waitFor(FRAME_TITLE);
        JFrame jFrame = (JFrame) jFrameOp.getSource();
        JSliderOperator[] ops = new JSliderOperator[4];
        for (int i = 0; i < ops.length; i++) {
            JSlider jSlider = JSliderOperator.findJSlider(jFrame, i);
            assertThat(jSlider).isNotNull();
            ops[i] = JSliderOperator.of(jSlider);
            assertThat(JSliderOperator.waitFor(jFrameOp, i).getSource()).isSameAs(ops[i].getSource());
        }

        JLabel label = JLabelOperator.findJLabel(jFrame, "0", StringComparators.strict());
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
