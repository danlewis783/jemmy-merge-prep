package org.netbeans.jemmy.operators;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
class ScrollPaneOperatorTest {



    private Frame frame;
    private Panel panel;
    private ScrollPane scrollPane;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                panel = new Panel();
                panel.setSize(400, 300);
                scrollPane = new ScrollPane();
                scrollPane.add(panel);
                scrollPane.setName("ScrollPaneOperatorTest");
                frame.add(scrollPane);
                frame.setSize(200, 100);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame.setVisible(false);
                frame.dispose();
                frame = null;
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructor() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        ScrollPaneOperator operator2 = new ScrollPaneOperator(operator,
                                           PredicatesJ.byName("ScrollPaneOperatorTest"));
       assertNotNull(operator2);
    }

    @Test
    void testFindScrollPane() {
        ScrollPane scrollPane1 = ScrollPaneOperator.findScrollPane(frame);
       assertNotNull(scrollPane1);
        ScrollPane scrollPane2 = ScrollPaneOperator.findScrollPane(frame,
                                     PredicatesJ.byName("ScrollPaneOperatorTest"));
       assertNotNull(scrollPane2);
    }

    @Test
    void testFindScrollPaneUnder() {
        ScrollPane scrollPane1 = ScrollPaneOperator.findScrollPaneUnder(frame);
       assertNull(scrollPane1);
    }

    @Test
    void testWaitScrollPane() {
        ScrollPane scrollPane1 = ScrollPaneOperator.waitScrollPane(frame);
       assertNotNull(scrollPane1);
        ScrollPane scrollPane2 = ScrollPaneOperator.waitScrollPane(frame,
                                     PredicatesJ.byName("ScrollPaneOperatorTest"));
       assertNotNull(scrollPane2);
    }

    @Test
    void testSetValues() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setValues(0, 0);
    }

    @Test
    void testScrollTo() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollTo(new ScrollAdjusterTest());
    }

    @Test
    void testScrollToHorizontalValue() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToHorizontalValue(0);
        operator1.scrollToHorizontalValue(0.0);
    }

    @Test
    void testScrollToVerticalValue() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToVerticalValue(0);
        operator1.scrollToVerticalValue(0.0);
    }

    @Test
    void testScrollToValues() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToValues(0, 0);
        operator1.scrollToValues(0.0, 0.0);
    }

    @Test
    void testScrollToTop() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToTop();
    }

    @Test
    void testScrollToBottom() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToBottom();
    }

    @Test
    void testScrollToLeft() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToLeft();
    }

    @Test
    void testScrollToRight() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToRight();
    }

    @Test
    void testScrollToComponentRectangle() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToComponentRectangle(panel, 1, 1, 10, 10);
    }

    @Test
    void testScrollToComponentPoint() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToComponentPoint(panel, 7, 5);
    }

    @Test
    void testScrollToComponent() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
    }

    @Test
    void testCheckInside() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.checkInside(panel);
    }

    @Test
    void testIsScrollbarVisible() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.isScrollbarVisible(Scrollbar.HORIZONTAL);
        operator1.isScrollbarVisible(Scrollbar.VERTICAL);
    }

    @Test
    void testGetHAdjustable() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getHAdjustable();
    }

    @Test
    void testGetHScrollbarHeight() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getHScrollbarHeight();
    }

    @Test
    void testGetScrollPosition() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.setScrollPosition(operator1.getScrollPosition());
        operator1.setScrollPosition(operator1.getScrollPosition().x, operator1.getScrollPosition().y);
    }

    @Test
    void testGetScrollbarDisplayPolicy() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getScrollbarDisplayPolicy();
    }

    @Test
    void testGetVAdjustable() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getVAdjustable();
    }

    @Test
    void testGetVScrollbarWidth() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getVScrollbarWidth();
    }

    @Test
    void testGetViewportSize() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.getViewportSize();
    }

    @Test
    void testParamString() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollPaneOperator operator1 = new ScrollPaneOperator(operator);
       assertNotNull(operator1);
        operator1.paramString();
    }


class ScrollAdjusterTest implements ScrollAdjuster {
        @Override
        public int getScrollDirection() {
            return 0;
        }

        @Override
        public int getScrollOrientation() {
            return 0;
        }
    }
}
