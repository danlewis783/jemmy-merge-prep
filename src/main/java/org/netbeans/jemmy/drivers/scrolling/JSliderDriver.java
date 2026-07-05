
package org.netbeans.jemmy.drivers.scrolling;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JSliderOperator;
import org.netbeans.jemmy.operators.Operator;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JSliderDriver extends AbstractScrollDriver {
    public JSliderDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JSliderOperator"));
    }

    @Override
    public void scrollToMinimum(ComponentOperator oper, int orientation) {
        checkSupported(oper);
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((JSliderOperator) oper).getMinimum() < ((JSliderOperator) oper).getValue()
                       ? DECREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return ((JSliderOperator) oper).getOrientation();
            }
        });
    }

    @Override
    public void scrollToMaximum(ComponentOperator oper, int orientation) {
        checkSupported(oper);
        scroll(oper, new ScrollAdjuster() {
            @Override
            public int getScrollDirection() {
                return ((JSliderOperator) oper).getMaximum() > ((JSliderOperator) oper).getValue()
                       ? INCREASE_SCROLL_DIRECTION : DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
            @Override
            public int getScrollOrientation() {
                return ((JSliderOperator) oper).getOrientation();
            }
        });
    }

    @Override
    protected void step(ComponentOperator oper, ScrollAdjuster adj) {
        if (adj.getScrollDirection() != ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION) {
            QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
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
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
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
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
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
        return TimeoutKey.JSliderOperator_ScrollingDelta;
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
        return 0;
    }

    private Point getClickPoint(ComponentOperator oper, int direction, int orientation) {
        int x, y;
        boolean inverted = ((JSliderOperator) oper).getInverted();
        int realDirection;
        if (inverted) {
            if (direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                realDirection = ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else if (direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                realDirection = ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else {
                return null;
            }
        } else {
            realDirection = direction;
        }

        if (orientation == JSlider.HORIZONTAL) {
            if (realDirection == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                x = oper.getWidth() - 1;
            } else if (realDirection == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                x = 0;
            } else {
                return null;
            }

            y = oper.getHeight() / 2;
        } else if (orientation == JSlider.VERTICAL) {
            if (realDirection == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) {
                y = 0;
            } else if (realDirection == ScrollAdjuster.DECREASE_SCROLL_DIRECTION) {
                y = oper.getHeight() - 1;
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
