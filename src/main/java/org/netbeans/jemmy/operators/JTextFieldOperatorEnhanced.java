package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.JTextField;

public class JTextFieldOperatorEnhanced extends JTextFieldOperator {

    JTextFieldOperatorEnhanced(JTextField b) {
        super(b);
    }

    public static JTextFieldOperatorEnhanced of(JTextField b) {
        return new JTextFieldOperatorEnhanced(b);
    }

    public static JTextFieldOperatorEnhanced waitFor(ContainerOperator cont, int index) {
        return of((JTextField) waitComponent(cont, PredicatesJ.of(JTextField.class), index));
    }

    @Override
    public void enterText(String text) {
        checkEnabledEditableAndVisible();
        super.enterText(text);
    }

    @Override
    public void setText(String text) {
        checkEnabledEditableAndVisible();
        super.setText(text);
    }

    private void checkEnabledEditableAndVisible() {
        boolean[] state = QueueTool.getInstance().callOnQueue(() ->
                new boolean[] {isEnabled(), isEditable(), isVisible()});
        boolean enabled = state[0];
        boolean editable = state[1];
        boolean visible = state[2];

        if (! (enabled && editable && visible)) {
            throw new IllegalStateException("Field " + getName() + " is " + (enabled ? "" : "not ") + "enabled, "
                    + (editable ? "" : "not ") + "editable, and " + (visible ? "" : "not ")
                    + "visible. All these were expected to be true.");
        }
    }
}
