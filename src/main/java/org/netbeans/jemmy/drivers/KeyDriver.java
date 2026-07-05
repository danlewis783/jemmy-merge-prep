
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;

public interface KeyDriver {
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers);

    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers);

    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, TimeoutKey pushTime);

    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, TimeoutKey pushTime);
}
