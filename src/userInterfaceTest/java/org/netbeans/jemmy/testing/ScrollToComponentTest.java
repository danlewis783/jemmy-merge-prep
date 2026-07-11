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

import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JScrollPaneOperator;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_018
class ScrollToComponentTest {

    @Test
    void test() {
        ButtonGridScrollApp.main();
        ComponentOperator.setDefaultComponentVisualizer(new EmptyVisualizer());
        JFrame jFrame = JFrameOperator.waitJFrame("ButtonGridScrollApp");
        JButton butt00 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "00", StringComparators.strict()));
        ComponentOperator butt00Op = ComponentOperator.of(butt00);
        JButtonOperator.findJButton(jFrame, "04", StringComparators.strict());
        JButton butt11 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "11", StringComparators.strict()));
        ComponentOperator butt11Op = ComponentOperator.of(butt11);
        JButton butt22 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "22", StringComparators.strict()));
        ComponentOperator butt22Op = ComponentOperator.of(butt22);
        JButton butt24 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "24", StringComparators.strict()));
        ComponentOperator butt24Op = ComponentOperator.of(butt24);
        JButton butt33 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "33", StringComparators.strict()));
        ComponentOperator butt33Op = ComponentOperator.of(butt33);
        JButton butt44 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "44", StringComparators.strict()));
        ComponentOperator butt44Op = ComponentOperator.of(butt44);
        JButton butt42 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "42", StringComparators.strict()));
        ComponentOperator butt42Op = ComponentOperator.of(butt42);
        JButton butt40 = Objects.requireNonNull(JButtonOperator.findJButton(jFrame, "40", StringComparators.strict()));
        ComponentOperator butt40Op = ComponentOperator.of(butt40);
        JScrollPane sp =
                Objects.requireNonNull(JScrollPaneOperator.findJScrollPane(jFrame, ComponentPredicates.alwaysTrue()));
        assertThat(JScrollPaneOperator.findJScrollPaneUnder(butt00)).isSameAs(sp);
        JScrollBarOperator hscroll = JScrollBarOperator.waitFor(JFrameOperator.of(jFrame), 1);
        assertThat(hscroll.getOrientation()).isEqualTo(JScrollBar.HORIZONTAL);
        JScrollBarOperator vscroll = JScrollBarOperator.waitFor(JFrameOperator.of(jFrame));
        assertThat(vscroll.getOrientation()).isEqualTo(JScrollBar.VERTICAL);
        JScrollPaneOperator scroller = JScrollPaneOperator.of(sp);
        assertThat(JScrollPaneOperator.waitFor(JFrameOperator.of(jFrame)).getSource())
                .isSameAs(scroller.getSource());
        scroller.setValues(
                scroller.getHorizontalScrollBar().getMaximum(),
                scroller.getVerticalScrollBar().getMaximum());
        assertThat(CheckInside.isInside(butt44Op, scroller, 0, 0, butt44Op.getWidth(), butt44Op.getHeight()))
                .isTrue();
        scroller.setValues(0, 0);
        assertThat(CheckInside.isInside(butt00Op, scroller, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        scroller.scrollToComponentPoint(butt22, butt22Op.getWidth() / 2, butt22Op.getHeight() / 2);
        assertThat(CheckInside.isInside(
                        butt22Op, scroller, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        scroller.scrollToRight();
        assertThat(CheckInside.isInside(
                        butt24Op, scroller, butt24Op.getWidth() / 2 - 1, butt24Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        int x22 = 10;
        int y22 = 10;
        int w22 = butt22Op.getWidth() - 20;
        int h22 = butt22Op.getHeight() - 20;
        scroller.scrollToComponentRectangle(butt22, x22, y22, w22, h22);
        assertThat(CheckInside.isInside(butt22Op, scroller, x22, y22, w22, h22)).isTrue();
        scroller.scrollToBottom();
        int x42 = 10;
        int y42 = 10;
        int w42 = butt42Op.getWidth() - 20;
        int h42 = butt42Op.getHeight() - 20;
        assertThat(CheckInside.isInside(butt42Op, scroller, x42, y42, w42, h42)).isTrue();
        scroller.scrollToLeft();
        assertThat(CheckInside.isInside(butt40Op, scroller, 0, 0, butt40Op.getWidth(), butt40Op.getHeight()))
                .isTrue();
        scroller.scrollToTop();
        assertThat(CheckInside.isInside(butt00Op, scroller, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        scroller.scrollToValues(0.5, 0.5);
        assertThat(CheckInside.isInside(butt22Op, scroller, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();
        scroller.scrollToComponent(butt11);
        assertThat(CheckInside.isInside(butt11Op, scroller, 0, 0, butt11Op.getWidth(), butt11Op.getHeight()))
                .isTrue();
        scroller.scrollToComponent(butt33);
        assertThat(CheckInside.isInside(butt33Op, scroller, 0, 0, butt33Op.getWidth(), butt33Op.getHeight()))
                .isTrue();
        scroller.getHScrollBarOperator()
                .scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        scroller.getVScrollBarOperator()
                .scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        assertThat(CheckInside.isInside(butt22Op, scroller, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();

        testJScrollBar(hscroll);
        testJScrollPane(scroller);
    }

    private void testJScrollBar(JScrollBarOperator jScrollBarOperator) {
        JScrollBar src = (JScrollBar) jScrollBarOperator.getSource();
        assertThat(jScrollBarOperator.getBlockIncrement()).isEqualTo(onQueue(src::getBlockIncrement));
        assertThat(jScrollBarOperator.getMaximum()).isEqualTo(onQueue(src::getMaximum));
        assertThat(jScrollBarOperator.getMinimum()).isEqualTo(onQueue(src::getMinimum));
        assertThat(jScrollBarOperator.getModel()).isEqualTo(onQueue(src::getModel));
        assertThat(jScrollBarOperator.getOrientation()).isEqualTo(onQueue(src::getOrientation));
        assertThat(jScrollBarOperator.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(jScrollBarOperator.getUnitIncrement()).isEqualTo(onQueue(src::getUnitIncrement));
        assertThat(jScrollBarOperator.getValue()).isEqualTo(onQueue(src::getValue));
        assertThat(jScrollBarOperator.getValueIsAdjusting()).isEqualTo(onQueue(src::getValueIsAdjusting));
        assertThat(jScrollBarOperator.getVisibleAmount()).isEqualTo(onQueue(src::getVisibleAmount));
    }

    private void testJScrollPane(JScrollPaneOperator jScrollPaneOperator) {
        JScrollPane src = (JScrollPane) jScrollPaneOperator.getSource();
        assertThat(jScrollPaneOperator.getColumnHeader()).isEqualTo(onQueue(src::getColumnHeader));
        assertThat(jScrollPaneOperator.getHorizontalScrollBar()).isEqualTo(onQueue(src::getHorizontalScrollBar));
        assertThat(jScrollPaneOperator.getHorizontalScrollBarPolicy())
                .isEqualTo(onQueue(src::getHorizontalScrollBarPolicy));
        assertThat(jScrollPaneOperator.getRowHeader()).isEqualTo(onQueue(src::getRowHeader));
        assertThat(jScrollPaneOperator.getUI()).isEqualTo(onQueue(src::getUI));
        assertThat(jScrollPaneOperator.getVerticalScrollBar()).isEqualTo(onQueue(src::getVerticalScrollBar));
        assertThat(jScrollPaneOperator.getVerticalScrollBarPolicy())
                .isEqualTo(onQueue(src::getVerticalScrollBarPolicy));
        assertThat(jScrollPaneOperator.getViewport()).isEqualTo(onQueue(src::getViewport));
        assertThat(jScrollPaneOperator.getViewportBorder()).isEqualTo(onQueue(src::getViewportBorder));
        assertThat(jScrollPaneOperator.getViewportBorderBounds()).isEqualTo(onQueue(src::getViewportBorderBounds));
    }
}
