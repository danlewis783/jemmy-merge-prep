package org.netbeans.jemmy.operators;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.*;
import javax.swing.plaf.ScrollPaneUI;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
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
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        JScrollPaneOperator operator2 = new JScrollPaneOperator(operator,
                                            PredicatesJ.byName("JScrollPaneOperatorTest"));
       assertNotNull(operator2);
    }

    @Test
    void testFindJScrollPane() {
        frame.setVisible(true);
        JScrollPane scrollPane1 = JScrollPaneOperator.findJScrollPane(frame);
       assertNotNull(scrollPane1);
        JScrollPane scrollPane2 = JScrollPaneOperator.findJScrollPane(frame,
                                      PredicatesJ.byName("JScrollPaneOperatorTest"));
       assertNotNull(scrollPane2);
    }

    @Test
    void testFindJScrollPaneUnder() {}

    @Test
    void testWaitJScrollPane() {
        frame.setVisible(true);
        JScrollPane scrollPane1 = JScrollPaneOperator.waitJScrollPane(frame);
       assertNotNull(scrollPane1);
        JScrollPane scrollPane2 = JScrollPaneOperator.waitJScrollPane(frame,
                                      PredicatesJ.byName("JScrollPaneOperatorTest"));
       assertNotNull(scrollPane2);
    }

    @Test
    void testSetValues() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setValues(1, 100);
    }

    @Test
    void testScrollToHorizontalValue() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToHorizontalValue(0);
        operator1.scrollToHorizontalValue(0.0);
    }

    @Test
    void testScrollToVerticalValue() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToVerticalValue(0);
        operator1.scrollToVerticalValue(0.0);
    }

    @Test
    void testScrollToValues() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToValues(0, 0);
        operator1.scrollToValues(0.0, 0.0);
    }

    @Test
    void testScrollToTop() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToTop();
    }

    @Test
    void testScrollToBottom() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToBottom();
    }

    @Test
    void testScrollToLeft() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToLeft();
    }

    @Test
    void testScrollToRight() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToRight();
    }

    @Test
    void testScrollToComponentRectangle() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToComponentRectangle(textArea, 0, 0, 10, 10);
    }

    @Test
    void testScrollToComponentPoint() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToComponentPoint(textArea, 10, 10);
    }

    @Test
    void testScrollToComponent() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToComponent(textArea);
    }

    @Test
    void testGetHScrollBarOperator() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
       assertNotNull(operator1.getHScrollBarOperator());
    }

    @Test
    void testGetVScrollBarOperator() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
       assertNotNull(operator1.getVScrollBarOperator());
    }

    @Test
    void testCheckInside() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.checkInside(textArea);
    }

    @Test
    void testCreateHorizontalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.createHorizontalScrollBar();
    }

    @Test
    void testCreateVerticalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.createVerticalScrollBar();
    }

    @Test
    void testGetColumnHeader() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setColumnHeader(null);
       assertNull(operator1.getColumnHeader());
    }

    @Test
    void testGetCorner() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
       assertNotNull(operator1.getCorner(JScrollPane.LOWER_LEFT_CORNER));
    }

    @Test
    void testGetHorizontalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setHorizontalScrollBar(new JScrollBar());
        operator1.getHorizontalScrollBar();
    }

    @Test
    void testGetHorizontalScrollBarPolicy() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        operator1.getHorizontalScrollBarPolicy();
    }

    @Test
    void testGetRowHeader() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setRowHeader(null);
        operator1.getRowHeader();
    }

    @Test
    void testGetUI() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        ScrollPaneUITest ui = new ScrollPaneUITest();
        operator1.setUI(ui);
       assertEquals(ui, operator1.getUI());
    }

    @Test
    void testGetVerticalScrollBar() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setVerticalScrollBar(new JScrollBar());
        operator1.getVerticalScrollBar();
    }

    @Test
    void testGetVerticalScrollBarPolicy() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        operator1.getVerticalScrollBarPolicy();
    }

    @Test
    void testGetViewport() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setViewport(null);
        operator1.getViewport();
    }

    @Test
    void testGetViewportBorder() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setViewportBorder(null);
        operator1.getViewportBorder();
    }

    @Test
    void testGetViewportBorderBounds() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getViewportBorderBounds();
    }

    @Test
    void testSetColumnHeaderView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setColumnHeaderView(null);
    }

    @Test
    void testSetRowHeaderView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setRowHeaderView(null);
    }

    @Test
    void testSetViewportView() {
        frame.setVisible(true);
        JFrameOperator operator = new JFrameOperator();
       assertNotNull(operator);
        JScrollPaneOperator operator1 = new JScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setViewportView(null);
    }

    private class ScrollPaneUITest extends ScrollPaneUI {}
}
