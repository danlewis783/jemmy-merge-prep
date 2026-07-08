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

import java.awt.Button;
import java.util.Objects;
import javax.swing.JFrame;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.ButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.ScrollPaneOperator;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_035
class AwtScrollPaneScrollingTest {

    @Test
    void test() {
        AwtButtonGridScrollApp.main();
        ComponentOperator.setDefaultComponentVisualizer(new EmptyVisualizer());
        JFrame jFrame = JFrameOperator.waitJFrame("AwtButtonGridScrollApp");
        Button butt00 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "00", StringComparators.strict()));
        Button butt04 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "04", StringComparators.strict()));
        Button butt11 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "11", StringComparators.strict()));
        Button butt22 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "22", StringComparators.strict()));
        Button butt24 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "24", StringComparators.strict()));
        Button butt33 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "33", StringComparators.strict()));
        Button butt44 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "44", StringComparators.strict()));
        Button butt42 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "42", StringComparators.strict()));
        Button butt40 = Objects.requireNonNull(ButtonOperator.findButton(jFrame, "40", StringComparators.strict()));
        ScrollPaneOperator scrollPaneOp = new ScrollPaneOperator(new JFrameOperator(jFrame));
        assertThat(ScrollPaneOperator.findScrollPaneUnder(butt00)).isSameAs(scrollPaneOp.getSource());
        scrollPaneOp.setValues(
                scrollPaneOp.getHAdjustable().getMaximum(),
                scrollPaneOp.getVAdjustable().getMaximum());
        ComponentOperator butt44Op = new ComponentOperator(butt44);
        assertThat(CheckInside.isInside(butt44Op, scrollPaneOp, 0, 0, butt44Op.getWidth(), butt44Op.getHeight()))
                .isTrue();
        scrollPaneOp.setValues(0, 0);
        ComponentOperator butt00Op = new ComponentOperator(butt00);
        assertThat(CheckInside.isInside(butt00Op, scrollPaneOp, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        ComponentOperator butt22Op = new ComponentOperator(butt22);
        scrollPaneOp.scrollToComponentPoint(butt22, butt22Op.getWidth() / 2, butt22Op.getHeight() / 2);
        assertThat(CheckInside.isInside(
                        butt22Op, scrollPaneOp, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        scrollPaneOp.scrollToRight();
        ComponentOperator butt24Op = new ComponentOperator(butt24);
        assertThat(CheckInside.isInside(
                        butt24Op, scrollPaneOp, butt24Op.getWidth() / 2 - 1, butt24Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        int x22 = 10;
        int y22 = 10;
        int w22 = butt22Op.getWidth() - 20;
        int h22 = butt22Op.getHeight() - 20;
        scrollPaneOp.scrollToComponentRectangle(butt22, x22, y22, w22, h22);
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, x22, y22, w22, h22))
                .isTrue();
        scrollPaneOp.scrollToBottom();
        int x42 = 10;
        int y42 = 10;
        ComponentOperator butt42Op = new ComponentOperator(butt42);
        int w42 = butt42Op.getWidth() - 20;
        int h42 = butt42Op.getHeight() - 20;
        assertThat(CheckInside.isInside(butt42Op, scrollPaneOp, x42, y42, w42, h42))
                .isTrue();
        scrollPaneOp.scrollToLeft();
        ComponentOperator butt40Op = new ComponentOperator(butt40);
        assertThat(CheckInside.isInside(butt40Op, scrollPaneOp, 0, 0, butt40Op.getWidth(), butt40Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToTop();
        assertThat(CheckInside.isInside(butt00Op, scrollPaneOp, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToValues(0.5, 0.5);
        assertThat(CheckInside.isInside(
                        butt22Op, scrollPaneOp, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 1, 1))
                .isTrue();
        scrollPaneOp.scrollToComponent(butt11);
        ComponentOperator butt11Op = new ComponentOperator(butt11);
        assertThat(CheckInside.isInside(butt11Op, scrollPaneOp, 0, 0, butt11Op.getWidth(), butt11Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToComponent(butt33);
        ComponentOperator butt33Op = new ComponentOperator(butt33);
        assertThat(CheckInside.isInside(butt33Op, scrollPaneOp, 0, 0, butt33Op.getWidth(), butt33Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToHorizontalValue(0.5);
        scrollPaneOp.scrollToVerticalValue(0.5);
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();
    }
}
