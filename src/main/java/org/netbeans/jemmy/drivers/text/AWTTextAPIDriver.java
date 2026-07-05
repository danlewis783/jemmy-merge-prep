
package org.netbeans.jemmy.drivers.text;

import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;

import java.util.Collections;

public final class AWTTextAPIDriver extends TextAPIDriver {
    public AWTTextAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.TextComponentOperator"));
    }

    @Override
    public String getText(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getText();
    }

    @Override
    public int getCaretPosition(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getCaretPosition();
    }

    @Override
    public int getSelectionStart(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionStart();
    }

    @Override
    public int getSelectionEnd(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionEnd();
    }
}
