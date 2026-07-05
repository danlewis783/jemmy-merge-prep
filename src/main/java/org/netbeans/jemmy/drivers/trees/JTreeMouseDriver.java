
package org.netbeans.jemmy.drivers.trees;

import org.netbeans.jemmy.*;
import org.netbeans.jemmy.drivers.*;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.concurrent.Callable;

public final class JTreeMouseDriver extends LightSupportiveDriver implements TreeDriver {
    private final QueueTool queueTool;

    public JTreeMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTreeOperator"));
        queueTool = QueueTool.getInstance();
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        selectItems(oper, new int[] { index });
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        ((JTreeOperator) oper).clearSelection();
        checkSupported(oper);
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        JTreeOperator toper = (JTreeOperator) oper;
        for (int i = 0; i < indices.length; i++) {
            int index = i;
            if (!EventQueue.isDispatchThread()) {
                toper.scrollToRow(indices[i]);
            }

            Point p = toper.getPointToClick(indices[index]);
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                mdriver.clickMouse(oper, p.x, p.y, 1, Operator.getDefaultMouseButton(),
                                   (index == 0) ? 0 : InputEvent.CTRL_MASK, TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
        }
    }

    @Override
    public void expandItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        JTreeOperator toper = (JTreeOperator) oper;
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        if (!toper.isExpanded(index)) {
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                Point p = toper.getPointToClick(index);
                mdriver.clickMouse(toper, p.x, p.y, 2, Operator.getDefaultMouseButton(), 0,
                                   TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
        }
    }

    @Override
    public void collapseItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        JTreeOperator toper = (JTreeOperator) oper;
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        if (toper.isExpanded(index)) {
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                Point p = toper.getPointToClick(index);
                mdriver.clickMouse(toper, p.x, p.y, 2, Operator.getDefaultMouseButton(), 0,
                                   TimeoutKey.ComponentOperator_MouseClickTimeout);
                return null;
            }));
        }
    }

    @Override
    public void editItem(ComponentOperator oper, int index, Object newValue, TimeoutKey waitEditorTime) {
        JTextComponentOperator textoper = startEditingAndReturnEditor(oper, index, waitEditorTime);
        TextDriver text =
            DriverManager.newInstance(JemmyProperties.getInstance()).getTextDriver(JTextComponentOperator.class);
        text.clearText(textoper);
        text.typeText(textoper, newValue.toString(), 0);
        DriverManager.newInstance(JemmyProperties.getInstance()).getKeyDriver(oper).pushKey(textoper,
                                  KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }

    @Override
    public void startEditing(ComponentOperator oper, int index, TimeoutKey waitEditorTime) {
        startEditingAndReturnEditor(oper, index, waitEditorTime);
    }

    private JTextComponentOperator startEditingAndReturnEditor(ComponentOperator oper, int index,
            TimeoutKey waitEditorTime) {
        checkSupported(oper);
        JTreeOperator toper = (JTreeOperator) oper;
        MouseDriver mdriver = DriverManager.newInstance(JemmyProperties.getInstance()).getMouseDriver(oper);
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point p = toper.getPointToClick(index);
            mdriver.clickMouse(toper, p.x, p.y, 1, Operator.getDefaultMouseButton(), 0,
                               TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
        Timeouts.sleep(TimeoutKey.JTreeOperator_BeforeEditTimeout);
        queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
            Point p = toper.getPointToClick(index);
            mdriver.clickMouse(toper, p.x, p.y, 1, Operator.getDefaultMouseButton(), 0,
                    TimeoutKey.ComponentOperator_MouseClickTimeout);
            return null;
        }));
        return new JTextComponentOperator(
            (JTextComponent) toper.waitSubComponent(PredicatesJ.of(JTextComponent.class), waitEditorTime));
    }
}
