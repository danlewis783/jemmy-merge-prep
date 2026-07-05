
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.operators.Operator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JListMouseDriver extends LightSupportiveDriver implements MultiSelListDriver {
    public JListMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JListOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        clickOnItem((JListOperator) oper, index);
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        clickOnItem((JListOperator) oper, indices[0]);

        for (int i = 1; i < indices.length; i++) {
            clickOnItem((JListOperator) oper, indices[i], InputEvent.CTRL_MASK);
        }
    }

    protected void clickOnItem(JListOperator oper, int index) {
        clickOnItem(oper, index, 0);
    }

    protected void clickOnItem(JListOperator oper, int index, int modifiers) {
        if (!EventQueue.isDispatchThread()) {
            oper.scrollToItem(index);
        }

        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Rectangle rect = oper.getCellBounds(index, index);
            DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper,
                                      rect.x + rect.width / 2, rect.y + rect.height / 2, 1,
                                      Operator.getDefaultMouseButton(), modifiers,
                                      TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
    }
}
