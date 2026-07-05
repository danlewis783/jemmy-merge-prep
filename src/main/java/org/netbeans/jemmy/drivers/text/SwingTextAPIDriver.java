
package org.netbeans.jemmy.drivers.text;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;

import java.util.Collections;

public final class SwingTextAPIDriver extends TextAPIDriver {
    public SwingTextAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTextComponentOperator"));
    }

    @Override
    public String getText(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getDisplayedText();
    }

    @Override
    public int getCaretPosition(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getCaretPosition();
    }

    @Override
    public int getSelectionStart(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getSelectionStart();
    }

    @Override
    public int getSelectionEnd(ComponentOperator oper) {
        return ((JTextComponentOperator) oper).getSelectionEnd();
    }
}
