
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.OrderedListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTableHeaderOperator;
import org.netbeans.jemmy.operators.Operator;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JTableHeaderDriver extends LightSupportiveDriver implements OrderedListDriver {
    public JTableHeaderDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTableHeaderOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        clickOnHeader((JTableHeaderOperator) oper, index);
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        clickOnHeader((JTableHeaderOperator) oper, indices[0]);

        for (int i = 1; i < indices.length; i++) {
            clickOnHeader((JTableHeaderOperator) oper, indices[i], InputEvent.CTRL_MASK);
        }
    }

    @Override
    public void moveItem(ComponentOperator oper, int moveColumn, int moveTo) {
        Point start = ((JTableHeaderOperator) oper).getPointToClick(moveColumn);
        Point end = ((JTableHeaderOperator) oper).getPointToClick(moveTo);
        oper.dragNDrop(start.x, start.y, end.x, end.y);
    }

    protected void clickOnHeader(JTableHeaderOperator oper, int index) {
        clickOnHeader(oper, index, 0);
    }

    protected void clickOnHeader(JTableHeaderOperator oper, int index, int modifiers) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point toClick = oper.getPointToClick(index);
            DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper,
                    toClick.x, toClick.y, 1, Operator.getDefaultMouseButton(), modifiers,
                    TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
    }
}
