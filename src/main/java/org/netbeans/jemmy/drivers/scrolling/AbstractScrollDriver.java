
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

import java.awt.*;
import java.util.List;

public abstract class AbstractScrollDriver extends LightSupportiveDriver implements ScrollDriver {
    public static final int ADJUST_CLICK_COUNT = 10;

    public AbstractScrollDriver(List<String> supported) {
        super(supported);
    }

    @Override
    public void scroll(ComponentOperator oper, ScrollAdjuster adj) {
        if (canJump(oper)) {
            doJumps(oper, adj);
        }

        if (canDragAndDrop(oper)) {
            doDragAndDrop(oper, adj);
        }

        if (canPushAndWait(oper)) {
            doPushAndWait(oper, adj);
        }

        for (int i = 0; i < ADJUST_CLICK_COUNT; i++) {
            doSteps(oper, adj);
        }
    }

    protected abstract void step(ComponentOperator oper, ScrollAdjuster adj);

    protected abstract void jump(ComponentOperator oper, ScrollAdjuster adj);

    protected abstract void startPushAndWait(ComponentOperator oper, int direction, int orientation);

    protected abstract void stopPushAndWait(ComponentOperator oper, int direction, int orientation);

    protected abstract Point startDragging(ComponentOperator oper);

    protected abstract void drop(ComponentOperator oper, Point pnt);

    protected abstract void drag(ComponentOperator oper, Point pnt);

    protected abstract TimeoutKey getScrollDeltaTimeout(ComponentOperator oper);

    protected abstract boolean canDragAndDrop(ComponentOperator oper);

    protected abstract boolean canJump(ComponentOperator oper);

    protected abstract boolean canPushAndWait(ComponentOperator oper);

    protected abstract int getDragAndDropStepLength(ComponentOperator oper);

    protected void doDragAndDrop(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            Point pnt = startDragging(oper);
            while (adj.getScrollDirection() == direction) {
                drag(oper, pnt = increasePoint(oper, pnt, adj, direction));
            }

            drop(oper, pnt);
        }
    }

    protected void doJumps(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            while (adj.getScrollDirection() == direction) {
                jump(oper, adj);
            }
        }
    }

    protected void doPushAndWait(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        int orientation = adj.getScrollOrientation();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            TimeoutKey delta = getScrollDeltaTimeout(oper);
            startPushAndWait(oper, direction, orientation);

            while (adj.getScrollDirection() == direction) {
                Timeouts.sleep(delta);
            }

            stopPushAndWait(oper, direction, orientation);
        }
    }

    protected void doSteps(ComponentOperator oper, ScrollAdjuster adj) {
        int direction = adj.getScrollDirection();
        if (direction != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            while (adj.getScrollDirection() == direction) {
                step(oper, adj);
            }
        }
    }

    private Point increasePoint(ComponentOperator oper, Point pnt, ScrollAdjuster adj, int direction) {
        return (adj.getScrollOrientation() == Adjustable.HORIZONTAL)
               ? new Point(pnt.x + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(oper), pnt.y)
               : new Point(pnt.x, pnt.y + ((direction == 1) ? 1 : -1) * getDragAndDropStepLength(oper));
    }
}
