
package org.netbeans.jemmy.drivers.buttons;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import java.util.Collections;

public final class ButtonMouseDriver extends LightSupportiveDriver implements ButtonDriver {
    public ButtonMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ComponentOperator"));
    }

    @Override
    public void press(ComponentOperator oper) {
        MouseDriver mouseDriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        mouseDriver.moveMouse(oper, oper.getCenterXForClick(), oper.getCenterYForClick());
        mouseDriver.pressMouse(oper, oper.getCenterXForClick(), oper.getCenterYForClick(),
                               Operator.getDefaultMouseButton(), 0);
    }

    @Override
    public void release(ComponentOperator oper) {
        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).releaseMouse(oper,
                                  oper.getCenterXForClick(), oper.getCenterYForClick(),
                                  Operator.getDefaultMouseButton(), 0);
    }

    @Override
    public void push(ComponentOperator oper) {
        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper,
                                  oper.getCenterXForClick(), oper.getCenterYForClick(), 1,
                                  Operator.getDefaultMouseButton(), 0,
                                  TimeoutKey.ComponentOperator_MouseClickTimeout);
    }
}
