package org.netbeans.jemmy.util;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.operators.WindowOperator;

import java.awt.*;

public final class MouseVisualizer extends DefaultVisualizer {
    private final double pointLocation;
    private final int depth;

    public MouseVisualizer(double pointLocation, int depth) {
        this.pointLocation = pointLocation;
        this.depth = depth;
    }

    @Override
    protected boolean isWindowActive(WindowOperator op) {
        return super.isWindowActive(op)
               && ((op.getSource() instanceof Frame) || (op.getSource() instanceof Dialog));
    }

    @Override
    protected void makeWindowActive(WindowOperator winOp) {
        Timeouts.sleep(TimeoutKey.MouseVisualiser_BeforeClickTimeout);
        super.makeWindowActive(winOp);
        Point p = getClickPoint(winOp);
        MouseRobotDriver driver = new MouseRobotDriver(TimeoutKey.EventDispatcher_RobotAutoDelay);
        driver.clickMouse(winOp, p.x, p.y, 1, Operator.getDefaultMouseButton(), 0,
                TimeoutKey.ComponentOperator_MouseClickTimeout);
    }

    private Point getClickPoint(WindowOperator winOp) {
        int winW = winOp.getWidth();
        int winH = winOp.getHeight();
        int x = (int) (winW * pointLocation - 1);
        int y = depth;

        if (x < 0) {
            x = 0;
        }

        if (x >= winW) {
            x = winW - 1;
        }

        if (y < 0) {
            y = 0;
        }

        if (y >= winH) {
            y = winH - 1;
        }

        return new Point(x, y);
    }
}
