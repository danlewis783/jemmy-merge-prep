
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JSliderOperator;

import java.awt.*;
import java.util.Collections;

public final class JSliderAPIDriver extends AbstractScrollDriver {
    private static final int SMALL_INCREMENT = 1;

    public JSliderAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JSliderOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        ((JSliderOperator) oper).setValue(((JSliderOperator) oper).getMinimum());
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        ((JSliderOperator) oper).setValue(((JSliderOperator) oper).getMaximum());
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        JSliderOperator scroll = (JSliderOperator) oper;
        int newValue = -1;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() > scroll.getMinimum() + getUnitIncrement(scroll))
                       ? scroll.getValue() - getUnitIncrement(scroll) : scroll.getMinimum();
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() < scroll.getMaximum() - getUnitIncrement(scroll))
                       ? scroll.getValue() + getUnitIncrement(scroll) : scroll.getMaximum();
        }

        setValue(oper, newValue);
    }

    private void setValue(ComponentOperator oper, int value) {
        if (value != -1) {
            ((JSliderOperator) oper).setValue(value);
        }
    }

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
        return TimeoutKey.JSliderOperator_ScrollingDelta;
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {
        JSliderOperator scroll = (JSliderOperator) oper;
        int newValue = -1;
        if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() > scroll.getMinimum() + getBlockIncrement(scroll))
                       ? scroll.getValue() - getBlockIncrement(scroll) : scroll.getMinimum();
        } else if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
            newValue = (scroll.getValue() < scroll.getMaximum() - getBlockIncrement(scroll))
                       ? scroll.getValue() + getBlockIncrement(scroll) : scroll.getMaximum();
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
        return isSmallIncrement((JSliderOperator) oper);
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return false;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        return 1;
    }

    private int getUnitIncrement(JSliderOperator oper) {
        return (oper.getMinorTickSpacing() == 0) ? 1 : oper.getMinorTickSpacing();
    }

    private int getBlockIncrement(JSliderOperator oper) {
        return (oper.getMajorTickSpacing() == 0) ? 1 : oper.getMajorTickSpacing();
    }

    private boolean isSmallIncrement(JSliderOperator oper) {
        return (oper.getMajorTickSpacing() <= SMALL_INCREMENT) && (oper.getMajorTickSpacing() <= SMALL_INCREMENT);
    }
}
