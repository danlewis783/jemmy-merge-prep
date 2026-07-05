
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

import java.util.List;

public final class MouseRobotDriver extends RobotDriver implements MouseDriver {
    public MouseRobotDriver(TimeoutKey robotAutoDelay) {
        super(robotAutoDelay);
    }

    public MouseRobotDriver(TimeoutKey robotAutoDelay, boolean smooth) {
        super(robotAutoDelay, smooth);
    }

    public MouseRobotDriver(TimeoutKey robotAutoDelay, List<String> supported) {
        super(robotAutoDelay, supported);
    }

    public MouseRobotDriver(TimeoutKey robotAutoDelay, List<String> supported, boolean smooth) {
        super(robotAutoDelay, supported, smooth);
    }

    @Override
    public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        pressMouse(mouseButton, modifiers);
    }

    @Override
    public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        releaseMouse(mouseButton, modifiers);
    }

    @Override
    public void moveMouse(ComponentOperator oper, int x, int y) {
        moveMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y));
    }

    @Override
    public void clickMouse(ComponentOperator oper, int x, int y, int clickCount, int mouseButton, int modifiers,
                           TimeoutKey mouseClick) {
        clickMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y), clickCount, mouseButton, modifiers, mouseClick);
    }

    @Override
    public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers) {
        moveMouse(getAbsoluteX(oper, x), getAbsoluteY(oper, y));
    }

    @Override
    public void dragNDrop(ComponentOperator oper, int start_x, int start_y, int end_x, int end_y, int mouseButton,
                          int modifiers, TimeoutKey before, TimeoutKey after) {
        dragNDrop(getAbsoluteX(oper, start_x), getAbsoluteY(oper, start_y), getAbsoluteX(oper, end_x),
                  getAbsoluteY(oper, end_y), mouseButton, modifiers, before, after);
    }

    @Override
    public void enterMouse(ComponentOperator oper) {
        moveMouse(oper, oper.getCenterXForClick(), oper.getCenterYForClick());
    }

    @Override
    public void exitMouse(ComponentOperator oper) {}

    protected int getAbsoluteX(ComponentOperator oper, int x) {
        return oper.getSource().getLocationOnScreen().x + x;
    }

    protected int getAbsoluteY(ComponentOperator oper, int y) {
        return oper.getSource().getLocationOnScreen().y + y;
    }
}
