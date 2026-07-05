
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.Operator;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JTabMouseDriver extends LightSupportiveDriver implements ListDriver {
    public JTabMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTabbedPaneOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        if (index != -1) {
            QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
                Rectangle rect = ((JTabbedPaneOperator) oper).getUI().getTabBounds((JTabbedPane) oper.getSource(),
                                     index);
                DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper,
                                          (int) (rect.getX() + rect.getWidth() / 2),
                                          (int) (rect.getY() + rect.getHeight() / 2), 1,
                                          Operator.getDefaultMouseButton(), 0,
                                          TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
        }
    }
}
