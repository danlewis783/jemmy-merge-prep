
package org.netbeans.jemmy.drivers.focus;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class MouseFocusDriver extends LightSupportiveDriver implements FocusDriver {
    public MouseFocusDriver() {
        super(Collections.unmodifiableList(
                Arrays.asList("org.netbeans.jemmy.operators.JListOperator",
                "org.netbeans.jemmy.operators.JScrollBarOperator",
                "org.netbeans.jemmy.operators.JSliderOperator",
                "org.netbeans.jemmy.operators.JTableOperator",
                "org.netbeans.jemmy.operators.JTextComponentOperator",
                "org.netbeans.jemmy.operators.JTreeOperator",
                "org.netbeans.jemmy.operators.ListOperator",
                "org.netbeans.jemmy.operators.ScrollbarOperator",
                "org.netbeans.jemmy.operators.TextAreaOperator",
                "org.netbeans.jemmy.operators.TextComponentOperator",
                "org.netbeans.jemmy.operators.TextFieldOperator")));
    }

    @Override
    public void giveFocus(ComponentOperator compOp) {
        if (!compOp.hasFocus()) {
            QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
                DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
                MouseDriver mouseDriver = driverManager.getMouseDriver(compOp);
                int defaultMouseButton = Operator.getDefaultMouseButton();
                int centerXForClick = compOp.getCenterXForClick();
                int centerYForClick = compOp.getCenterYForClick();
                mouseDriver.clickMouse(compOp, centerXForClick, centerYForClick, 1, defaultMouseButton, 0,
                                       TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
            compOp.waitHasFocus();
        }
    }
}
