
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JScrollBarOperator;
import org.netbeans.jemmy.operators.Operator;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JScrollBarDriver extends AbstractScrollDriver {
    private static final int MINIMAL_DRAGGER_SIZE = 5;
    private static final int RELATIVE_DRAG_STEP_LENGTH = 20;
    private static final int SMALL_INCREMENT = 1;

    public JScrollBarDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JScrollBarOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        startDragging(oper);
        Point pnt = new Point(0, 0);
        drag(oper, pnt);
        while (((JScrollBarOperator) oper).getValue() > ((JScrollBarOperator) oper).getMinimum()) {
            Timeouts.sleep(TimeoutKey.Waiter_TimeDelta);
        }

        drop(oper, pnt);
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        startDragging(oper);
        Point pnt = new Point(oper.getWidth() - 1, oper.getHeight() - 1);
        drag(oper, pnt);
        while (((JScrollBarOperator) oper).getValue()
                > ((JScrollBarOperator) oper).getMaximum() - ((JScrollBarOperator) oper).getVisibleAmount()) {
            Timeouts.sleep(TimeoutKey.Waiter_TimeDelta);
        }

        drop(oper, pnt);
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        JButtonOperator boper = findAButton(oper, adj.getScrollDirection());
        DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(boper).push(boper);
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {
        JButtonOperator lessButton = findAButton(oper, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator moreButton = findAButton(oper, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            if (adj.getScrollDirection() != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
                int x, y;
                if (((JScrollBarOperator) oper).getOrientation() == JScrollBar.HORIZONTAL) {
                    if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                        x = moreButton.getX() - 1;
                    } else if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                        x = lessButton.getX() + lessButton.getWidth();
                    } else {
                        return null;
                    }

                    y = lessButton.getHeight() / 2;
                } else if (((JScrollBarOperator) oper).getOrientation() == JScrollBar.VERTICAL) {
                    if (adj.getScrollDirection() == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                        y = moreButton.getY() - 1;
                    } else if (adj.getScrollDirection() == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                        y = lessButton.getY() + lessButton.getHeight();
                    } else {
                        return null;
                    }

                    x = lessButton.getWidth() / 2;
                } else {
                    return null;
                }

                DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper, x,
                        y, 1, Operator.getDefaultMouseButton(), 0, TimeoutKey.JScrollBar_Jump);
            }

            return null;
        }));
    }

    @Override
    protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {
        JButtonOperator boper = findAButton(oper, direction);
        DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(boper).press(boper);
    }

    @Override
    protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {
        JButtonOperator boper = findAButton(oper, direction);
        DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(boper).release(boper);
    }

    @Override
    protected Point startDragging(ComponentOperator oper) {
        JButtonOperator lessButton = findAButton(oper, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator moreButton = findAButton(oper, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        Point pnt = getClickPoint((JScrollBarOperator) oper, lessButton, moreButton,
                                  ((JScrollBarOperator) oper).getValue());
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        mdriver.moveMouse(oper, pnt.x, pnt.y);
        mdriver.pressMouse(oper, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        return pnt;
    }

    @Override
    protected void drop(ComponentOperator oper, Point pnt) {
        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).releaseMouse(oper, pnt.x, pnt.y,
                                  Operator.getDefaultMouseButton(), 0);
    }

    @Override
    protected void drag(ComponentOperator oper, Point pnt) {
        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).dragMouse(oper, pnt.x, pnt.y,
                                  Operator.getDefaultMouseButton(), 0);
    }

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
        return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected boolean canDragAndDrop(ComponentOperator oper) {
        if (!isSmallIncrement((JScrollBarOperator) oper)) {
            return false;
        }

        boolean result = false;
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        JButtonOperator less = findAButton(oper, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator more = findAButton(oper, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        Point pnt = getClickPoint((JScrollBarOperator) oper, less, more, ((JScrollBarOperator) oper).getValue());
        mdriver.moveMouse(oper, pnt.x, pnt.y);
        mdriver.pressMouse(oper, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        result = ((JScrollBarOperator) oper).getValueIsAdjusting();
        mdriver.releaseMouse(oper, pnt.x, pnt.y, Operator.getDefaultMouseButton(), 0);
        return result && isSmallIncrement((JScrollBarOperator) oper);
    }

    @Override
    protected boolean canJump(ComponentOperator oper) {
        return isSmallIncrement((JScrollBarOperator) oper);
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return isSmallIncrement((JScrollBarOperator) oper);
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        JButtonOperator less = findAButton(oper, ScrollAdjuster.DECREASE_SCROLL_DIRECTION);
        JButtonOperator more = findAButton(oper, ScrollAdjuster.INCREASE_SCROLL_DIRECTION);
        int width = oper.getWidth() - less.getWidth() - more.getWidth();
        int height = oper.getHeight() - less.getHeight() - more.getHeight();
        int max = (width > height) ? width : height;
        if (max >= RELATIVE_DRAG_STEP_LENGTH * 2) {
            return max / RELATIVE_DRAG_STEP_LENGTH;
        } else {
            return 1;
        }
    }

    private boolean isSmallIncrement(JScrollBarOperator oper) {
        return (oper.getUnitIncrement(-1) <= SMALL_INCREMENT) && (oper.getUnitIncrement(1) <= SMALL_INCREMENT);
    }

    private Point getClickPoint(JScrollBarOperator oper, JButtonOperator lessButton, JButtonOperator moreButton,
                                int value) {
        int lenght = (oper.getOrientation() == JScrollBar.HORIZONTAL)
                     ? oper.getWidth() - lessButton.getWidth() - moreButton.getWidth()
                     : oper.getHeight() - lessButton.getHeight() - moreButton.getHeight();
        int subpos = (int) ((float) lenght / (oper.getMaximum() - oper.getMinimum()) * value);
        if (oper.getOrientation() == JScrollBar.HORIZONTAL) {
            subpos = subpos + lessButton.getWidth();
        } else {
            subpos = subpos + lessButton.getHeight();
        }

        subpos = subpos + MINIMAL_DRAGGER_SIZE / 2 + 1;
        return (oper.getOrientation() == JScrollBar.HORIZONTAL)
               ? new Point(subpos, oper.getHeight() / 2) : new Point(oper.getWidth() / 2, subpos);
    }

    private JButtonOperator findAButton(ComponentOperator oper, int direction) {
        return (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION)
               ? ((JScrollBarOperator) oper).getDecreaseButton() : ((JScrollBarOperator) oper).getIncreaseButton();
    }
}
