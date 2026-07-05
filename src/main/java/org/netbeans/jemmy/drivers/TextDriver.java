
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface TextDriver {
    public void changeCaretPosition(ComponentOperator oper, int position);

    public void selectText(ComponentOperator oper, int startPosition, int finalPosition);

    public void clearText(ComponentOperator oper);

    public void typeText(ComponentOperator oper, String text, int caretPosition);

    public void changeText(ComponentOperator oper, String text);

    public void enterText(ComponentOperator oper, String text);
}
