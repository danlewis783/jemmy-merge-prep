
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;

import java.awt.*;
import java.util.Collections;

public final class JScrollBarAPIDriver extends AbstractScrollDriver {
    private static final int SMALL_INCREMENT = 1;

    public JScrollBarAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JScrollBarOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        JScrollBarOperator scroll = (JScrollBarOperator) oper;
        setValue(oper, scroll.getMinimum());
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        JScrollBarOperator scroll = (JScrollBarOperator) oper;
        setValue(oper, scroll.getMaximum() - scroll.getVisibleAmount());
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        JScrollBarOperator scroll = (JScrollBarOperator) oper;
        int newValue = -1;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() > scroll.getMinimum() + scroll.getUnitIncrement())
                       ? scroll.getValue() - scroll.getUnitIncrement() : scroll.getMinimum();
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue()
                        < scroll.getMaximum() - scroll.getVisibleAmount()
                          - scroll.getUnitIncrement()) ? scroll.getValue() + scroll.getUnitIncrement()
                              : scroll.getMaximum();
        }

        setValue(oper, newValue);
    }

    private void setValue(ComponentOperator oper, int value) {
        if (value != -1) {
            ((JScrollBarOperator) oper).setValue(value);
        }
    }

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
        return TimeoutKey.JScrollBarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {
        JScrollBarOperator scroll = (JScrollBarOperator) oper;
        int newValue = -1;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() > scroll.getMinimum() + scroll.getBlockIncrement())
                       ? scroll.getValue() - scroll.getBlockIncrement() : scroll.getMinimum();
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue()
                        < scroll.getMaximum() - scroll.getVisibleAmount()
                          - scroll.getBlockIncrement()) ? scroll.getValue() + scroll.getBlockIncrement()
                              : scroll.getMaximum();
        }

        setValue(oper, newValue);
    }

    @Override
    protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {}

    @Override
    protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {}

    @Override
    protected Point startDragging(ComponentOperator oper) {
        return null;
    }

    @Override
    protected void drop(ComponentOperator oper, Point pnt) {}

    @Override
    protected void drag(ComponentOperator oper, Point pnt) {}

    @Override
    protected boolean canDragAndDrop(ComponentOperator oper) {
        return false;
    }

    @Override
    protected boolean canJump(ComponentOperator oper) {
        return isSmallIncrement((JScrollBarOperator) oper);
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return false;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        return 1;
    }

    private boolean isSmallIncrement(JScrollBarOperator oper) {
        return (oper.getUnitIncrement(-1) <= SMALL_INCREMENT) && (oper.getUnitIncrement(1) <= SMALL_INCREMENT);
    }
}
