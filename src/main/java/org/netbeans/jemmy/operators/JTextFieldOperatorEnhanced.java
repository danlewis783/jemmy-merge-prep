package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.predicates.ComponentPredicates;

import javax.swing.JTextField;

public class JTextFieldOperatorEnhanced extends JTextFieldOperator {

    JTextFieldOperatorEnhanced(JTextField b) {
        super(b);
    }

    public static JTextFieldOperatorEnhanced of(JTextField b) {
        return new JTextFieldOperatorEnhanced(b);
    }

    public static JTextFieldOperatorEnhanced waitFor(ContainerOperator cont, int index) {
        return of((JTextField) waitComponent(cont, ComponentPredicates.of(JTextField.class), index));
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
        boolean enabled = isEnabled();
        boolean editable = isEditable();
        boolean visible = isVisible();

        if (! (enabled && editable && visible)) {
            throw new IllegalStateException("Field " + getName() + " is " + (enabled ? "" : "not ") + "enabled, "
                    + (editable ? "" : "not ") + "editable, and " + (visible ? "" : "not ")
                    + "visible. All these were expected to be true.");
        }
    }
}
