
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ScrollbarOperator;

import java.awt.*;
import java.util.Collections;

public final class ScrollbarDriver extends AWTScrollDriver {
    private static final int CLICK_OFFSET = 5;

    public ScrollbarDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ScrollbarOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((ScrollbarOperator) oper).getMinimum() < ((ScrollbarOperator) oper).getValue()
                       ? DECREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return ((ScrollbarOperator) oper).getOrientation();
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((ScrollbarOperator) oper).getMaximum() - ((ScrollbarOperator) oper).getVisibleAmount()
                       > ((ScrollbarOperator) oper).getValue() ? INCREASE_SCROLL_DIRECTION
                           : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return ((ScrollbarOperator) oper).getOrientation();
            }
        });
    }

    @Override
    protected Point getClickPoint(ComponentOperator oper, int direction, int orientation) {
        int x, y;
        if (orientation == Scrollbar.HORIZONTAL) {
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                x = oper.getWidth() - 1 - CLICK_OFFSET;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                x = CLICK_OFFSET;
            } else {
                return null;
            }

            y = oper.getHeight() / 2;
        } else if (orientation == Scrollbar.VERTICAL) {
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                y = oper.getHeight() - 1 - CLICK_OFFSET;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                y = CLICK_OFFSET;
            } else {
                return null;
            }

            x = oper.getWidth() / 2;
        } else {
            return null;
        }

        return new Point(x, y);
    }
}
