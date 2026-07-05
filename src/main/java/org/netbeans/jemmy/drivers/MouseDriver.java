
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;

public interface MouseDriver {
    public void pressMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers);

    public void releaseMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers);

    public void clickMouse(ComponentOperator oper, int x, int y, int clickCount, int mouseButton, int modifiers,
                           TimeoutKey mouseClick);

    public void moveMouse(ComponentOperator oper, int x, int y);

    public void dragMouse(ComponentOperator oper, int x, int y, int mouseButton, int modifiers);

    public void dragNDrop(ComponentOperator oper, int start_x, int start_y, int end_x, int end_y, int mouseButton,
                          int modifiers, TimeoutKey before, TimeoutKey after);

    public void enterMouse(ComponentOperator oper);

    public void exitMouse(ComponentOperator oper);
}
