package org.netbeans.jemmy.operators;


import org.junit.jupiter.api.*;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
class ScrollbarOperatorTest {



    private Frame frame;
    private Scrollbar scrollbar;

    @BeforeEach
    void beforeEach() {
        try {
            EventQueue.invokeAndWait(() -> {
                frame = new Frame();
                scrollbar = new Scrollbar();
                scrollbar.setName("ScrollbarOperatorTest");
                frame.add(scrollbar);
                frame.setSize(30, 300);
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
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        ScrollbarOperator operator2 = new ScrollbarOperator(operator, PredicatesJ.byName("ScrollbarOperatorTest"));
       assertNotNull(operator2);
    }

    @Test
    void testFindScrollbar() {
        Scrollbar scrollbar1 = ScrollbarOperator.findScrollbar(frame);
       assertNotNull(scrollbar1);
        Scrollbar scrollbar2 = ScrollbarOperator.findScrollbar(frame, PredicatesJ.byName("ScrollbarOperatorTest"));
       assertNotNull(scrollbar2);
    }

    @Test
    void testWaitScrollbar() {
        Scrollbar scrollbar1 = ScrollbarOperator.waitScrollbar(frame);
       assertNotNull(scrollbar1);
        Scrollbar scrollbar2 = ScrollbarOperator.waitScrollbar(frame, PredicatesJ.byName("ScrollbarOperatorTest"));
       assertNotNull(scrollbar2);
    }

    @Test
    void testScrollTo() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.scrollTo(new ScrollAdjusterTest());
        operator1.scrollTo(o -> Boolean.TRUE, null, false);
    }

    @Test
    @Disabled("FIXME")
    void testScrollToValue() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToValue(1.0);
        operator1.scrollToValue(1);
    }

    @Test
    @Disabled("FIXME")
    void testScrollToMinimum() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToMinimum();
    }

    @Test
    @Disabled("FIXME")
    void testScrollToMaximum() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.scrollToMaximum();
    }

    @Test
    void testAddAdjustmentListener() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        AdjustmentListenerTest listener = new AdjustmentListenerTest();
        operator1.addAdjustmentListener(listener);
        operator1.removeAdjustmentListener(listener);
    }

    @Test
    void testGetBlockIncrement() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setBlockIncrement(operator1.getBlockIncrement());
    }

    @Test
    void testGetMaximum() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setMaximum(operator1.getMaximum());
    }

    @Test
    void testGetMinimum() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setMinimum(operator1.getMinimum());
    }

    @Test
    void testGetOrientation() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setOrientation(operator1.getOrientation());
    }

    @Test
    void testGetUnitIncrement() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setUnitIncrement(operator1.getUnitIncrement());
    }

    @Test
    void testGetValue() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setValue(operator1.getValue());
    }

    @Test
    void testGetVisibleAmount() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setVisibleAmount(operator1.getVisibleAmount());
    }

    @Test
    void testSetValues() {
        FrameOperator operator = new FrameOperator();
       assertNotNull(operator);
        ScrollbarOperator operator1 = new ScrollbarOperator(operator);
       assertNotNull(operator1);
        operator1.setValues(0, 1, 0, 1);
    }

    private class AdjustmentListenerTest implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {}
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
