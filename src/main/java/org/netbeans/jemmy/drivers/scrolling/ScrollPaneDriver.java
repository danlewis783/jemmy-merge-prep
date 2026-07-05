
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ScrollPaneOperator;

import java.awt.*;
import java.util.Collections;

public final class ScrollPaneDriver extends AWTScrollDriver {
    private static final int CLICK_OFFSET = 5;

    public ScrollPaneDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ScrollPaneOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        Adjustable adj = (orientation == Scrollbar.HORIZONTAL)
                               ? ((ScrollPaneOperator) oper).getHAdjustable()
                               : ((ScrollPaneOperator) oper).getVAdjustable();
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return (adj.getMinimum() < adj.getValue()) ? DECREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return orientation;
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        Adjustable adj = (orientation == Scrollbar.HORIZONTAL)
                               ? ((ScrollPaneOperator) oper).getHAdjustable()
                               : ((ScrollPaneOperator) oper).getVAdjustable();
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return (adj.getMaximum() - adj.getVisibleAmount() > adj.getValue())
                       ? INCREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return orientation;
            }
        });
    }

    @Override
    protected Point getClickPoint(ComponentOperator oper, int direction, int orientation) {
        int x, y;
        if (orientation == Scrollbar.HORIZONTAL) {
            int offset = ((ScrollPaneOperator) oper).isScrollbarVisible(Scrollbar.VERTICAL)
                         ? ((ScrollPaneOperator) oper).getVScrollbarWidth() : 0;
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                x = oper.getWidth() - 1 - CLICK_OFFSET - offset;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                x = CLICK_OFFSET;
            } else {
                return null;
            }

            y = oper.getHeight() - ((ScrollPaneOperator) oper).getHScrollbarHeight() / 2;
        } else if (orientation == Scrollbar.VERTICAL) {
            int offset = ((ScrollPaneOperator) oper).isScrollbarVisible(Scrollbar.HORIZONTAL)
                         ? ((ScrollPaneOperator) oper).getHScrollbarHeight() : 0;
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                y = oper.getHeight() - 1 - CLICK_OFFSET - offset;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                y = CLICK_OFFSET;
            } else {
                return null;
            }

            x = oper.getWidth() - ((ScrollPaneOperator) oper).getVScrollbarWidth() / 2;
        } else {
            return null;
        }

        return new Point(x, y);
    }
}
