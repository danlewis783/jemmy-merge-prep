package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class jemmy_018 {




    @Test
    void test() {
        Application_018.main(new String[] {});
        ComponentOperator.setDefaultComponentVisualizer(new EmptyVisualizer());
        JFrame jFrame = JFrameOperator.waitJFrame("Application_018");
        JButton butt00 = JButtonOperator.findJButton(jFrame, "00", StringComparators.strict());
        ComponentOperator butt00Op = new ComponentOperator(butt00);
        JButtonOperator.findJButton(jFrame, "04", StringComparators.strict());
        JButton butt11 = JButtonOperator.findJButton(jFrame, "11", StringComparators.strict());
        ComponentOperator butt11Op = new ComponentOperator(butt11);
        JButton butt22 = JButtonOperator.findJButton(jFrame, "22", StringComparators.strict());
        ComponentOperator butt22Op = new ComponentOperator(butt22);
        JButton butt24 = JButtonOperator.findJButton(jFrame, "24", StringComparators.strict());
        ComponentOperator butt24Op = new ComponentOperator(butt24);
        JButton butt33 = JButtonOperator.findJButton(jFrame, "33", StringComparators.strict());
        ComponentOperator butt33Op = new ComponentOperator(butt33);
        JButton butt44 = JButtonOperator.findJButton(jFrame, "44", StringComparators.strict());
        ComponentOperator butt44Op = new ComponentOperator(butt44);
        JButton butt42 = JButtonOperator.findJButton(jFrame, "42", StringComparators.strict());
        ComponentOperator butt42Op = new ComponentOperator(butt42);
        JButton butt40 = JButtonOperator.findJButton(jFrame, "40", StringComparators.strict());
        ComponentOperator butt40Op = new ComponentOperator(butt40);
        JScrollPane sp = JScrollPaneOperator.findJScrollPane(jFrame, PredicatesJ.alwaysTrue());
        assertSame(sp, JScrollPaneOperator.findJScrollPaneUnder(butt00));
        JScrollBarOperator hscroll = new JScrollBarOperator(new JFrameOperator(jFrame), 1);
        assertEquals(JScrollBar.HORIZONTAL, hscroll.getOrientation());
        JScrollBarOperator vscroll = new JScrollBarOperator(new JFrameOperator(jFrame));
        assertEquals(JScrollBar.VERTICAL, vscroll.getOrientation());
        JScrollPaneOperator scroller = new JScrollPaneOperator(sp);
        assertSame(scroller.getSource(), new JScrollPaneOperator(new JFrameOperator(jFrame)).getSource());
        scroller.setValues(scroller.getHorizontalScrollBar().getMaximum(),
                           scroller.getVerticalScrollBar().getMaximum());
        assertTrue(CheckInside.isInside(butt44Op, scroller, 0, 0, butt44Op.getWidth(), butt44Op.getHeight()));
        scroller.setValues(0, 0);
        assertTrue(CheckInside.isInside(butt00Op, scroller, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()));
        scroller.scrollToComponentPoint(butt22, butt22Op.getWidth() / 2, butt22Op.getHeight() / 2);
        assertTrue(CheckInside.isInside(butt22Op, scroller, butt22Op.getWidth() / 2 - 1, butt22Op.getHeight() / 2 - 1, 2, 2));
        scroller.scrollToRight();
        assertTrue(CheckInside.isInside(butt24Op, scroller, butt24Op.getWidth() / 2 - 1, butt24Op.getHeight() / 2 - 1, 2, 2));
        int x22 = 10;
        int y22 = 10;
        int w22 = butt22Op.getWidth() - 20;
        int h22 = butt22Op.getHeight() - 20;
        scroller.scrollToComponentRectangle(butt22, x22, y22, w22, h22);
        assertTrue(CheckInside.isInside(butt22Op, scroller, x22, y22, w22, h22));
        scroller.scrollToBottom();
        int x42 = 10;
        int y42 = 10;
        int w42 = butt42Op.getWidth() - 20;
        int h42 = butt42Op.getHeight() - 20;
        assertTrue(CheckInside.isInside(butt42Op, scroller, x42, y42, w42, h42));
        scroller.scrollToLeft();
        assertTrue(CheckInside.isInside(butt40Op, scroller, 0, 0, butt40Op.getWidth(), butt40Op.getHeight()));
        scroller.scrollToTop();
        assertTrue(CheckInside.isInside(butt00Op, scroller, 0, 0, butt00Op.getWidth(), butt00Op.getHeight()));
        scroller.scrollToValues(0.5, 0.5);
        assertTrue(CheckInside.isInside(butt22Op, scroller, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()));
        scroller.scrollToComponent(butt11);
        assertTrue(CheckInside.isInside(butt11Op, scroller, 0, 0, butt11Op.getWidth(), butt11Op.getHeight()));
        scroller.scrollToComponent(butt33);
        assertTrue(CheckInside.isInside(butt33Op, scroller, 0, 0, butt33Op.getWidth(), butt33Op.getHeight()));
        scroller.getHScrollBarOperator().scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        scroller.getVScrollBarOperator().scrollTo(op -> Integer.compare((op.getMaximum() - op.getVisibleAmount()) / 2, op.getValue()));
        assertTrue(CheckInside.isInside(butt22Op, scroller, 0, 0, butt22Op.getWidth(), butt22Op.getHeight()));

        if (!testJScrollBar(hscroll)) {
            fail("did not work");
        }

        if (!testJScrollPane(scroller)) {
            fail("did not work");
        }
    }

    private boolean testJScrollBar(JScrollBarOperator jScrollBarOperator) {
        if (((JScrollBar) jScrollBarOperator.getSource()).getBlockIncrement()
                == jScrollBarOperator.getBlockIncrement()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getMaximum() == jScrollBarOperator.getMaximum()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getMinimum() == jScrollBarOperator.getMinimum()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getModel() == null && (jScrollBarOperator.getModel() == null)
                || ((JScrollBar) jScrollBarOperator.getSource()).getModel().equals(jScrollBarOperator.getModel())) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getOrientation() == jScrollBarOperator.getOrientation()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getUI() == null && (jScrollBarOperator.getUI() == null)
                || ((JScrollBar) jScrollBarOperator.getSource()).getUI().equals(jScrollBarOperator.getUI())) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getUnitIncrement()
                == jScrollBarOperator.getUnitIncrement()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getValue() == jScrollBarOperator.getValue()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getValueIsAdjusting()
                == jScrollBarOperator.getValueIsAdjusting()) {}
        else {
            return false;
        }

        if (((JScrollBar) jScrollBarOperator.getSource()).getVisibleAmount()
                == jScrollBarOperator.getVisibleAmount()) {}
        else {
            return false;
        }

        return true;
    }

    private boolean testJScrollPane(JScrollPaneOperator jScrollPaneOperator) {
        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getColumnHeader() == null && (jScrollPaneOperator
                    .getColumnHeader() == null) || ((JScrollPane) jScrollPaneOperator.getSource()).getColumnHeader()
                        .equals(jScrollPaneOperator.getColumnHeader())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getHorizontalScrollBar() == null && (jScrollPaneOperator
                    .getHorizontalScrollBar() == null) || ((JScrollPane) jScrollPaneOperator.getSource())
                        .getHorizontalScrollBar().equals(jScrollPaneOperator.getHorizontalScrollBar())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource()).getHorizontalScrollBarPolicy()
                == jScrollPaneOperator.getHorizontalScrollBarPolicy()) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getRowHeader() == null && (jScrollPaneOperator
                    .getRowHeader() == null) || ((JScrollPane) jScrollPaneOperator.getSource()).getRowHeader()
                        .equals(jScrollPaneOperator.getRowHeader())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource()).getUI() == null && (jScrollPaneOperator.getUI() == null)
                || ((JScrollPane) jScrollPaneOperator.getSource()).getUI().equals(jScrollPaneOperator.getUI())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getVerticalScrollBar() == null && (jScrollPaneOperator
                    .getVerticalScrollBar() == null) || ((JScrollPane) jScrollPaneOperator.getSource())
                        .getVerticalScrollBar().equals(jScrollPaneOperator.getVerticalScrollBar())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource()).getVerticalScrollBarPolicy()
                == jScrollPaneOperator.getVerticalScrollBarPolicy()) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getViewport() == null && (jScrollPaneOperator
                    .getViewport() == null) || ((JScrollPane) jScrollPaneOperator.getSource()).getViewport()
                        .equals(jScrollPaneOperator.getViewport())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getViewportBorder() == null && (jScrollPaneOperator
                    .getViewportBorder() == null) || ((JScrollPane) jScrollPaneOperator.getSource()).getViewportBorder()
                        .equals(jScrollPaneOperator.getViewportBorder())) {}
        else {
            return false;
        }

        if (((JScrollPane) jScrollPaneOperator.getSource())
                .getViewportBorderBounds() == null && (jScrollPaneOperator
                    .getViewportBorderBounds() == null) || ((JScrollPane) jScrollPaneOperator.getSource())
                        .getViewportBorderBounds().equals(jScrollPaneOperator.getViewportBorderBounds())) {}
        else {
            return false;
        }

        return true;
    }
}
