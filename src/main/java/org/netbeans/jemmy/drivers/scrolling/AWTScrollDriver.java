
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import java.awt.*;
import java.util.concurrent.Callable;

public abstract class AWTScrollDriver extends AbstractScrollDriver {
    private final QueueTool queueTool;

    public AWTScrollDriver(java.util.List<String> supported) {
        super(supported);
        queueTool = QueueTool.getInstance();
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        if (adj.getScrollDirection() != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                Point clickPoint = getClickPoint(oper, adj.getScrollDirection(), adj.getScrollOrientation());
                if (clickPoint != null) {
                    DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper,
                                              clickPoint.x, clickPoint.y, 1, Operator.getDefaultMouseButton(), 0,
                                              TimeoutKey.ComponentOperator_MouseClickTimeout);
                }

                return null;
            }));
        }
    }

    @Override
    protected void jump(ComponentOperator oper, ScrollAdjuster adj) {}

    @Override
    protected void startPushAndWait(ComponentOperator oper, int direction, int orientation) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point clickPoint = getClickPoint(oper, direction, orientation);
            if (clickPoint != null) {
                MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
                mdriver.moveMouse(oper, clickPoint.x, clickPoint.y);
                mdriver.pressMouse(oper, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
            }

            return null;
        }));
    }

    @Override
    protected void stopPushAndWait(ComponentOperator oper, int direction, int orientation) {
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point clickPoint = getClickPoint(oper, direction, orientation);
            if (clickPoint != null) {
                MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
                mdriver.releaseMouse(oper, clickPoint.x, clickPoint.y, Operator.getDefaultMouseButton(), 0);
            }

            return null;
        }));
    }

    @Override
    protected Point startDragging(ComponentOperator oper) {
        return null;
    }

    @Override
    protected void drop(ComponentOperator oper, Point pnt) {}

    @Override
    protected void drag(ComponentOperator oper, Point pnt) {}

    @Override
    protected TimeoutKey getScrollDeltaTimeout(ComponentOperator oper) {
        return TimeoutKey.ScrollbarOperator_DragAndDropScrollingDelta;
    }

    @Override
    protected boolean canDragAndDrop(ComponentOperator oper) {
        return false;
    }

    @Override
    protected boolean canJump(ComponentOperator oper) {
        return false;
    }

    @Override
    protected boolean canPushAndWait(ComponentOperator oper) {
        return true;
    }

    @Override
    protected int getDragAndDropStepLength(ComponentOperator oper) {
        return 1;
    }

    protected abstract Point getClickPoint(ComponentOperator oper, int direction, int orientation);
}
