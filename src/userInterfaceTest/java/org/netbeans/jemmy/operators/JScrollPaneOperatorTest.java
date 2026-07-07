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

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.ScrollPaneUI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

class JScrollPaneOperatorTest {
    private JFrame frame;
    private JTextArea textArea;

    @BeforeEach
    void beforeEach() {
        frame = new JFrame();
        textArea = new JTextArea("JTextArea");
        textArea.setSize(1000, 1000);
        textArea.setMaximumSize(new Dimension(1000, 1000));
        textArea.setMinimumSize(new Dimension(1000, 1000));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setName("JScrollPaneOperatorTest");
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollPane);
        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);
    }

    @AfterEach
    void afterEach() {
        frame.setVisible(false);
    }

    @Test
    void testConstructor() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        JScrollPaneOperator operator2 =
                new JScrollPaneOperator(operator, PredicatesJ.byName("JScrollPaneOperatorTest"));
        assertThat(operator2).isNotNull();
    }

    @Test
    void testFindJScrollPane() {
        frame.setVisible(true);
        JScrollPane scrollPane1 = JScrollPaneOperator.findJScrollPane(frame);
        assertThat(scrollPane1).isNotNull();
        JScrollPane scrollPane2 =
                JScrollPaneOperator.findJScrollPane(frame, PredicatesJ.byName("JScrollPaneOperatorTest"));
        assertThat(scrollPane2).isNotNull();
    }

    @Test
    void testFindJScrollPaneUnder() {}

    @Test
    void testWaitJScrollPane() {
        frame.setVisible(true);
        JScrollPane scrollPane1 = JScrollPaneOperator.waitJScrollPane(frame);
        assertThat(scrollPane1).isNotNull();
        JScrollPane scrollPane2 =
                JScrollPaneOperator.waitJScrollPane(frame, PredicatesJ.byName("JScrollPaneOperatorTest"));
        assertThat(scrollPane2).isNotNull();
    }

    @Test
    void testSetValues() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setValues(1, 100);
    }

    @Test
    void testScrollToHorizontalValue() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToHorizontalValue(0);
        operator1.scrollToHorizontalValue(0.0);
    }

    @Test
    void testScrollToVerticalValue() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToVerticalValue(0);
        operator1.scrollToVerticalValue(0.0);
    }

    @Test
    void testScrollToValues() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToValues(0, 0);
        operator1.scrollToValues(0.0, 0.0);
    }

    @Test
    void testScrollToTop() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToTop();
    }

    @Test
    void testScrollToBottom() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToBottom();
    }

    @Test
    void testScrollToLeft() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToLeft();
    }

    @Test
    void testScrollToRight() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToRight();
    }

    @Test
    void testScrollToComponentRectangle() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToComponentRectangle(textArea, 0, 0, 10, 10);
    }

    @Test
    void testScrollToComponentPoint() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToComponentPoint(textArea, 10, 10);
    }

    @Test
    void testScrollToComponent() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.scrollToComponent(textArea);
    }

    @Test
    void testGetHScrollBarOperator() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getHScrollBarOperator()).isNotNull();
    }

    @Test
    void testGetVScrollBarOperator() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        assertThat(operator1.getVScrollBarOperator()).isNotNull();
    }

    @Test
    void testCheckInside() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.checkInside(textArea);
    }

    @Test
    void testCreateHorizontalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.createHorizontalScrollBar();
    }

    @Test
    void testCreateVerticalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.createVerticalScrollBar();
    }

    @Test
    void testGetColumnHeader() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumnHeader(null);
        assertThat(operator1.getColumnHeader()).isNull();
    }

    @Test
    void testGetCorner() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
        assertThat(operator1.getCorner(JScrollPane.LOWER_LEFT_CORNER)).isNotNull();
    }

    @Test
    void testGetHorizontalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setHorizontalScrollBar(new JScrollBar());
        operator1.getHorizontalScrollBar();
    }

    @Test
    void testGetHorizontalScrollBarPolicy() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        operator1.getHorizontalScrollBarPolicy();
    }

    @Test
    void testGetRowHeader() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowHeader(null);
        operator1.getRowHeader();
    }

    @Test
    void testGetUI() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        ScrollPaneUITest ui = new ScrollPaneUITest();
        operator1.setUI(ui);
        assertThat(operator1.getUI()).isEqualTo(ui);
    }

    @Test
    void testGetVerticalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setVerticalScrollBar(new JScrollBar());
        operator1.getVerticalScrollBar();
    }

    @Test
    void testGetVerticalScrollBarPolicy() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        operator1.getVerticalScrollBarPolicy();
    }

    @Test
    void testGetViewport() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setViewport(null);
        operator1.getViewport();
    }

    @Test
    void testGetViewportBorder() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setViewportBorder(null);
        operator1.getViewportBorder();
    }

    @Test
    void testGetViewportBorderBounds() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.getViewportBorderBounds();
    }

    @Test
    void testSetColumnHeaderView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setColumnHeaderView(null);
    }

    @Test
    void testSetRowHeaderView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setRowHeaderView(null);
    }

    @Test
    void testSetViewportView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
        assertThat(operator).isNotNull();
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
        assertThat(operator1).isNotNull();
        operator1.setViewportView(null);
    }

    private class ScrollPaneUITest extends ScrollPaneUI {}
}
