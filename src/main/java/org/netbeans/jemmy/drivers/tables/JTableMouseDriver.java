
package org.netbeans.jemmy.drivers.tables;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TableDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JTableMouseDriver extends LightSupportiveDriver implements TableDriver {
    public JTableMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTableOperator"));
    }

    @Override
    public void selectCell(ComponentOperator oper, int row, int column) {
        clickOnCell((JTableOperator) oper, row, column, 1);
    }

    @Override
    public void editCell(ComponentOperator oper, int row, int column, Object value) {
        JTableOperator toper = (JTableOperator) oper;
        toper.scrollToCell(row, column);

        if (!toper.isEditing() || (toper.getEditingRow() != row) || (toper.getEditingColumn() != column)) {
            clickOnCell((JTableOperator) oper, row, column, 2);
        }

        JTextComponentOperator textoper =
            new JTextComponentOperator((JTextComponent) toper.waitSubComponent(PredicatesJ.of(JTextComponent.class)));
        TextDriver text =
            DriverManager.newInstance(JemmyProperties.getInstance()).getTextDriver(JTextComponentOperator.class);
        text.clearText(textoper);
        text.typeText(textoper, value.toString(), 0);
        DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper).pushKey(textoper,
                                  KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    protected void clickOnCell(JTableOperator oper, int row, int column, int clickCount) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point point = oper.getPointToClick(row, column);
            DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper).clickMouse(oper, point.x,
                                      point.y, clickCount, Operator.getDefaultMouseButton(), 0,
                                      TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
    }
}
