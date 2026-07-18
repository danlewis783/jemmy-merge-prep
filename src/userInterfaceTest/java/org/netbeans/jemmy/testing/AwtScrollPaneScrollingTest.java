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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.ScrollPane;
import javax.swing.JFrame;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.netbeans.jemmy.operators.ButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.ScrollPaneOperator;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparators;

// formerly scenario test jemmy_035
@Timeout(value=1, unit=TimeUnit.SECONDS)
class AwtScrollPaneScrollingTest {

    private JFrame jFrame;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            jFrame = new JFrame("AwtButtonGridScrollApp");
            Container contentPane = jFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            Panel panel = new Panel();
            panel.setLayout(new GridLayout(5, 5));
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.add(panel);
            contentPane.add(scrollPane, BorderLayout.CENTER);

            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    panel.add(new Button(String.valueOf(i) + j));
                }
            }

            jFrame.setSize(150, 150);
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
        JFrame frm = JFrameOperator.waitJFrame("AwtButtonGridScrollApp");
        Button butt00 = ButtonOperator.findButton(frm, "00", StringComparators.strict());
        assertThat(butt00).isNotNull();
        Button butt04 = ButtonOperator.findButton(frm, "04", StringComparators.strict());
        assertThat(butt04).isNotNull();
        Button butt11 = ButtonOperator.findButton(frm, "11", StringComparators.strict());
        assertThat(butt11).isNotNull();
        Button butt22 = ButtonOperator.findButton(frm, "22", StringComparators.strict());
        assertThat(butt22).isNotNull();
        Button butt24 = ButtonOperator.findButton(frm, "24", StringComparators.strict());
        assertThat(butt24).isNotNull();
        Button butt33 = ButtonOperator.findButton(frm, "33", StringComparators.strict());
        assertThat(butt33).isNotNull();
        Button butt44 = ButtonOperator.findButton(frm, "44", StringComparators.strict());
        assertThat(butt44).isNotNull();
        Button butt42 = ButtonOperator.findButton(frm, "42", StringComparators.strict());
        assertThat(butt42).isNotNull();
        Button butt40 = ButtonOperator.findButton(frm, "40", StringComparators.strict());
        assertThat(butt40).isNotNull();
        ScrollPaneOperator scrollPaneOp = ScrollPaneOperator.waitFor(JFrameOperator.of(frm));
        scrollPaneOp.setVisualizer(new EmptyVisualizer());
        assertThat(ScrollPaneOperator.findScrollPaneUnder(butt00)).isSameAs(scrollPaneOp.getSource());
        scrollPaneOp.setValues(
                scrollPaneOp.getHAdjustable().getMaximum(),
                scrollPaneOp.getVAdjustable().getMaximum());
        ComponentOperator butt44Op = ComponentOperator.of(butt44);
        assertThat(CheckInside.isInside(butt44Op, scrollPaneOp, 0, 0, butt44Op.getWidth(), butt44Op.getHeight()))
                .isTrue();
        scrollPaneOp.setValues(0, 0);
        ComponentOperator butt00Op = ComponentOperator.of(butt00);
        assertThat(CheckInside.isInside(butt00Op, scrollPaneOp, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()))
                .isTrue();
        ComponentOperator butt22Op = ComponentOperator.of(butt22);
        scrollPaneOp.scrollToComponentPoint(butt22, butt22Op.getWidth() / 2, butt22Op.getHeight() / 2);
        assertThat(CheckInside.isInside(
                        butt22Op, scrollPaneOp, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 2, 2))
                .isTrue();
        scrollPaneOp.scrollToRight();
        ComponentOperator butt24Op = ComponentOperator.of(butt24);
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
        ComponentOperator butt42Op = ComponentOperator.of(butt42);
        int w42 = butt42Op.getWidth() - 20;
        int h42 = butt42Op.getHeight() - 20;
        assertThat(CheckInside.isInside(butt42Op, scrollPaneOp, x42, y42, w42, h42))
                .isTrue();
        scrollPaneOp.scrollToLeft();
        ComponentOperator butt40Op = ComponentOperator.of(butt40);
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
        ComponentOperator butt11Op = ComponentOperator.of(butt11);
        assertThat(CheckInside.isInside(butt11Op, scrollPaneOp, 0, 0, butt11Op.getWidth(), butt11Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToComponent(butt33);
        ComponentOperator butt33Op = ComponentOperator.of(butt33);
        assertThat(CheckInside.isInside(butt33Op, scrollPaneOp, 0, 0, butt33Op.getWidth(), butt33Op.getHeight()))
                .isTrue();
        scrollPaneOp.scrollToHorizontalValue(0.5);
        scrollPaneOp.scrollToVerticalValue(0.5);
        assertThat(CheckInside.isInside(butt22Op, scrollPaneOp, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()))
                .isTrue();
    }
}
