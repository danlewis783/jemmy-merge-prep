
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ChoiceOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.Operator;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collections;

public final class ChoiceDriver extends LightSupportiveDriver implements ListDriver {
    private static final int RIGHT_INDENT = 10;

    public ChoiceDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ChoiceOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        ChoiceOperator choiceOperator = (ChoiceOperator) oper;
        Point pointToClick = getClickPoint(oper);
        DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper, pointToClick.x,
                                  pointToClick.y, 1, Operator.getDefaultMouseButton(), 0,
                                  TimeoutKey.ComponentOperator_MouseClickTimeout);
        KeyDriver keyDriver = DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper);
        while (choiceOperator.getSelectedIndex() != index) {
            keyDriver.pushKey(oper, (index > choiceOperator.getSelectedIndex()) ? KeyEvent.VK_DOWN : KeyEvent.VK_UP, 0,
                              TimeoutKey.ComponentOperator_PushKeyTimeout);
        }

        keyDriver.pushKey(oper, KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    private Point getClickPoint(ComponentOperator oper) {
        return new Point(oper.getWidth() - RIGHT_INDENT, oper.getHeight() / 2);
    }
}
