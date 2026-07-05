
package org.netbeans.jemmy.drivers.trees;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.drivers.TreeDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.text.JTextComponent;
import java.util.Collections;

public final class JTreeAPIDriver extends LightSupportiveDriver implements TreeDriver {
    public JTreeAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTreeOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        selectItems(oper, new int[] { index });
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        checkSupported(oper);
        ((JTreeOperator) oper).clearSelection();
        ((JTreeOperator) oper).addSelectionRows(indices);
    }

    @Override
    public void expandItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        ((JTreeOperator) oper).expandRow(index);
    }

    @Override
    public void collapseItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        ((JTreeOperator) oper).collapseRow(index);
    }

    @Override
    public void editItem(ComponentOperator oper, int index, Object newValue, TimeoutKey waitEditorTime) {
        JTextComponentOperator textoper = startEditingAndReturnEditor(oper, index, waitEditorTime);
        TextDriver text =
            DriverManager.newInstance(JemmyProperties.getInstance()).getTextDriver(JTextComponentOperator.class);
        text.clearText(textoper);
        text.typeText(textoper, newValue.toString(), 0);
        ((JTreeOperator) oper).stopEditing();
    }

    @Override
    public void startEditing(ComponentOperator oper, int index, TimeoutKey waitEditorTime) {
        startEditingAndReturnEditor(oper, index, waitEditorTime);
    }

    private JTextComponentOperator startEditingAndReturnEditor(ComponentOperator oper, int index,
            TimeoutKey waitEditorTime) {
        checkSupported(oper);
        JTreeOperator jTreeOperator = (JTreeOperator) oper;
        jTreeOperator.startEditingAtPath(jTreeOperator.getPathForRow(index));
        JTextComponent jTextComponent =
                (JTextComponent) jTreeOperator.waitSubComponent(PredicatesJ.of(JTextComponent.class), waitEditorTime);
        return new JTextComponentOperator(jTextComponent);
    }
}
