
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.DispatchingModel;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.input.KeyEventDriver;
import org.netbeans.jemmy.drivers.input.KeyRobotDriver;
import org.netbeans.jemmy.drivers.input.MouseEventDriver;
import org.netbeans.jemmy.drivers.input.MouseRobotDriver;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public final class InputDriverInstaller {
    private final JemmyProperties jemmyProperties;
    private final TimeoutKey robotAutoDelay;
    private final boolean smooth;
    private final boolean useEventDrivers;

    public InputDriverInstaller(EnumSet<DispatchingModel> model, TimeoutKey robotAutoDelay,
                                JemmyProperties jemmyProperties) {
        this.useEventDrivers = !model.contains(DispatchingModel.Robot);
        this.smooth = model.contains(DispatchingModel.SmoothRobot);
        this.robotAutoDelay = robotAutoDelay;
        this.jemmyProperties = jemmyProperties;
    }

    public void install() {
        DriverManager driverMgr = DriverManager.newInstance(jemmyProperties);
        if (useEventDrivers) {
            LightDriver keyE = new KeyEventDriver();
            LightDriver mouseE = new MouseEventDriver();
            driverMgr.removeDriver(DriverType.Key, keyE.getSupported());
            driverMgr.removeDriver(DriverType.Mouse, mouseE.getSupported());
            driverMgr.setDriver(DriverType.Key, keyE);
            driverMgr.setDriver(DriverType.Mouse, mouseE);

            try {
                List<String> awtOperators =
                        Collections.unmodifiableList(
                            Arrays.asList("org.netbeans.jemmy.operators.ButtonOperator",
                                    "org.netbeans.jemmy.operators.CheckboxOperator",
                                    "org.netbeans.jemmy.operators.ChoiceOperator",
                                    "org.netbeans.jemmy.operators.LabelOperator",
                                    "org.netbeans.jemmy.operators.ListOperator",
                                    "org.netbeans.jemmy.operators.ScrollPaneOperator",
                                    "org.netbeans.jemmy.operators.ScrollbarOperator",
                                    "org.netbeans.jemmy.operators.TextAreaOperator",
                                    "org.netbeans.jemmy.operators.TextComponentOperator",
                                    "org.netbeans.jemmy.operators.TextFieldOperator"));
                LightDriver keyR = new KeyRobotDriver(robotAutoDelay, awtOperators);
                LightDriver mouseR = new MouseRobotDriver(robotAutoDelay, awtOperators);
                driverMgr.removeDriver(DriverType.Key, keyR.getSupported());
                driverMgr.removeDriver(DriverType.Mouse, mouseR.getSupported());
                driverMgr.setDriver(DriverType.Key, keyR);
                driverMgr.setDriver(DriverType.Mouse, mouseR);
            } catch (JemmyException e) {
                if (!(e.getCause() instanceof ClassNotFoundException)) {
                    throw e;
                }
            }
        } else {
            LightDriver keyR = new KeyRobotDriver(robotAutoDelay);
            LightDriver mouseR = new MouseRobotDriver(robotAutoDelay, smooth);
            driverMgr.removeDriver(DriverType.Key, keyR.getSupported());
            driverMgr.removeDriver(DriverType.Mouse, mouseR.getSupported());
            driverMgr.setDriver(DriverType.Key, keyR);
            driverMgr.setDriver(DriverType.Mouse, mouseR);
        }
    }
}
