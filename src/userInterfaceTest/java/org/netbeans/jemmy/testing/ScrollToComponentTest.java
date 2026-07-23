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
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.JScrollPaneOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_018
// 10s rather than 2s: at higher display scaling the scaled content leaves the small viewport
// proportionally more to scroll through
@Timeout(value=10, unit=TimeUnit.SECONDS)
class ScrollToComponentTest {
    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JFrame jFrame = new JFrame("ScrollToComponentTest");
            this.jFrame = jFrame;

            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            JPanel pane = new JPanel();
            pane.setLayout(new GridLayout(7, 7));
            JScrollPane scrollPane = new JScrollPane(pane);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            contentPane.add(scrollPane, BorderLayout.CENTER);

            // 7x7 rather than 5x5, in a larger frame: the content must overflow the viewport in
            // both axes at 100% scaling, while the scrollbar tracks must stay longer than the
            // scaled minimum thumb at 150-200% (a track shorter than the minimum thumb hides
            // the thumb, and every track click then pages in the same direction)
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    pane.add(new JButton(String.valueOf(i) + j));
                }
            }

            jFrame.setSize(220, 220);
            TestWindows.place(jFrame);
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
        JFrame jFrame = JFrameOperator.waitJFrame("ScrollToComponentTest");
        JButton butt00 = JButtonOperator.findJButton(jFrame, "00", StringComparators.strict());
        assertThat(butt00).isNotNull();
        ComponentOperator butt00Op = ComponentOperator.of(butt00);
        JButtonOperator.findJButton(jFrame, "04", StringComparators.strict());
        JButton butt11 = JButtonOperator.findJButton(jFrame, "11", StringComparators.strict());
        assertThat(butt11).isNotNull();
        ComponentOperator butt11Op = ComponentOperator.of(butt11);
        JButton butt22 = JButtonOperator.findJButton(jFrame, "22", StringComparators.strict());
        assertThat(butt22).isNotNull();
        ComponentOperator butt22Op = ComponentOperator.of(butt22);
        JButton butt24 = JButtonOperator.findJButton(jFrame, "24", StringComparators.strict());
        assertThat(butt24).isNotNull();
        ComponentOperator butt24Op = ComponentOperator.of(butt24);
        JButton butt33 = JButtonOperator.findJButton(jFrame, "33", StringComparators.strict());
        assertThat(butt33).isNotNull();
        ComponentOperator butt33Op = ComponentOperator.of(butt33);
        JButton butt44 = JButtonOperator.findJButton(jFrame, "44", StringComparators.strict());
        assertThat(butt44).isNotNull();
        ComponentOperator butt44Op = ComponentOperator.of(butt44);
        JButton butt42 = JButtonOperator.findJButton(jFrame, "42", StringComparators.strict());
        assertThat(butt42).isNotNull();
        ComponentOperator butt42Op = ComponentOperator.of(butt42);
        JButton butt40 = JButtonOperator.findJButton(jFrame, "40", StringComparators.strict());
        assertThat(butt40).isNotNull();
        ComponentOperator butt40Op = ComponentOperator.of(butt40);
        JScrollPane scrollPane = JScrollPaneOperator.findJScrollPane(jFrame, PredicatesJ.alwaysTrue());
        assertThat(scrollPane).isNotNull();
        assertThat(JScrollPaneOperator.findJScrollPaneUnder(butt00)).isSameAs(scrollPane);
        JScrollBarOperator hScrollBarOp = JScrollBarOperator.waitFor(JFrameOperator.of(jFrame), 1);
        assertThat(hScrollBarOp.getOrientation()).isEqualTo(JScrollBar.HORIZONTAL);
        JScrollBarOperator vScrollBarOp = JScrollBarOperator.waitFor(JFrameOperator.of(jFrame));
        assertThat(vScrollBarOp.getOrientation()).isEqualTo(JScrollBar.VERTICAL);
        JScrollPaneOperator scrollPaneOp = JScrollPaneOperator.of(scrollPane);
        scrollPaneOp.setVisualizer(new EmptyVisualizer());
        assertThat(JScrollPaneOperator.waitFor(JFrameOperator.of(jFrame)).getSource())
                .isSameAs(scrollPaneOp.getSource());
        scrollPaneOp.setValues(
                scrollPaneOp.getHorizontalScrollBar().getMaximum(),
                scrollPaneOp.getVerticalScrollBar().getMaximum());
        assertThat(CheckInside.isInside(butt44Op, scrollPaneOp, 0, 0, butt44Op.getWidth(), butt44Op.getHeight()))
                .isTrue();
        scrollPaneOp.setValues(0, 0);
        assertThat(CheckInside.isInside(butt00Op, scrollPaneOp, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToComponentPoint(butt22, butt22Op.getWidth() / 2, butt22Op.getHeight() / 2);
        assertThat(CheckInside.isInside(
                        butt22Op, scrollPaneOp, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        scrollPaneOp.scrollToRight();
        assertThat(CheckInside.isInside(
                        butt24Op, scrollPaneOp, butt24Op.getWidth() / 2 - 1, butt24Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        int x22 = 10;
        int y22 = 10;
        int w22 = butt22Op.getWidth() - 20;
        int h22 = butt22Op.getHeight() - 20;
        scrollPaneOp.scrollToComponentRectangle(butt22, x22, y22, w22, h22);
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, x22, y22, w22, h22)).isTrue();
        scrollPaneOp.scrollToBottom();
        int x42 = 10;
        int y42 = 10;
        int w42 = butt42Op.getWidth() - 20;
        int h42 = butt42Op.getHeight() - 20;
        assertThat(CheckInside.isInside(butt42Op, scrollPaneOp, x42, y42, w42, h42)).isTrue();
        scrollPaneOp.scrollToLeft();
        assertThat(CheckInside.isInside(butt40Op, scrollPaneOp, 0, 0, butt40Op.getWidth(), butt40Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToTop();
        assertThat(CheckInside.isInside(butt00Op, scrollPaneOp, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToValues(0.5, 0.5);
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToComponent(butt11);
        assertThat(CheckInside.isInside(butt11Op, scrollPaneOp, 0, 0, butt11Op.getWidth(), butt11Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToComponent(butt33);
        assertThat(CheckInside.isInside(butt33Op, scrollPaneOp, 0, 0, butt33Op.getWidth(), butt33Op.getHeight()))
                .isTrue();
        scrollPaneOp.getHScrollBarOperator()
                .scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        scrollPaneOp.getVScrollBarOperator()
                .scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();

        // test scrollbar
        JScrollBar hScrollBar = (JScrollBar) hScrollBarOp.getSource();
        assertThat(hScrollBarOp.getBlockIncrement()).isEqualTo(onQueue(hScrollBar::getBlockIncrement));
        assertThat(hScrollBarOp.getMaximum()).isEqualTo(onQueue(hScrollBar::getMaximum));
        assertThat(hScrollBarOp.getMinimum()).isEqualTo(onQueue(hScrollBar::getMinimum));
        assertThat(hScrollBarOp.getModel()).isEqualTo(onQueue(hScrollBar::getModel));
        assertThat(hScrollBarOp.getOrientation()).isEqualTo(onQueue(hScrollBar::getOrientation));
        assertThat(hScrollBarOp.getUI()).isEqualTo(onQueue(hScrollBar::getUI));
        assertThat(hScrollBarOp.getUnitIncrement()).isEqualTo(onQueue(hScrollBar::getUnitIncrement));
        assertThat(hScrollBarOp.getValue()).isEqualTo(onQueue(hScrollBar::getValue));
        assertThat(hScrollBarOp.getValueIsAdjusting()).isEqualTo(onQueue(hScrollBar::getValueIsAdjusting));
        assertThat(hScrollBarOp.getVisibleAmount()).isEqualTo(onQueue(hScrollBar::getVisibleAmount));

        // test scrollpane
        assertThat(scrollPaneOp.getColumnHeader()).isEqualTo(onQueue(scrollPane::getColumnHeader));
        assertThat(scrollPaneOp.getHorizontalScrollBar()).isEqualTo(onQueue(scrollPane::getHorizontalScrollBar));
        assertThat(scrollPaneOp.getHorizontalScrollBarPolicy())
                .isEqualTo(onQueue(scrollPane::getHorizontalScrollBarPolicy));
        assertThat(scrollPaneOp.getRowHeader()).isEqualTo(onQueue(scrollPane::getRowHeader));
        assertThat(scrollPaneOp.getUI()).isEqualTo(onQueue(scrollPane::getUI));
        assertThat(scrollPaneOp.getVerticalScrollBar()).isEqualTo(onQueue(scrollPane::getVerticalScrollBar));
        assertThat(scrollPaneOp.getVerticalScrollBarPolicy())
                .isEqualTo(onQueue(scrollPane::getVerticalScrollBarPolicy));
        assertThat(scrollPaneOp.getViewport()).isEqualTo(onQueue(scrollPane::getViewport));
        assertThat(scrollPaneOp.getViewportBorder()).isEqualTo(onQueue(scrollPane::getViewportBorder));
        assertThat(scrollPaneOp.getViewportBorderBounds()).isEqualTo(onQueue(scrollPane::getViewportBorderBounds));
    }

}
