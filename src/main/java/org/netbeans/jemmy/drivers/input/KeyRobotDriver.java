
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

import java.util.List;

public final class KeyRobotDriver extends RobotDriver implements KeyDriver {
    public KeyRobotDriver(TimeoutKey robotAutoDelay) {
        super(robotAutoDelay);
    }

    public KeyRobotDriver(TimeoutKey robotAutoDelay, List<String> supported) {
        super(robotAutoDelay, supported);
    }

    @Override
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, TimeoutKey pushTime) {
        pressKey(oper, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        releaseKey(oper, keyCode, modifiers);
    }

    @Override
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, TimeoutKey pushTime) {
        pushKey(oper, keyCode, modifiers, pushTime);
    }

    @Override
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers) {
        pressKey(keyCode, modifiers);
    }

    @Override
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers) {
        releaseKey(keyCode, modifiers);
    }
}
