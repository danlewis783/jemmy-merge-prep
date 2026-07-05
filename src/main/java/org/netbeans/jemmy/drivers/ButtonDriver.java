
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface ButtonDriver {
    public void press(ComponentOperator oper);

    public void release(ComponentOperator oper);

    public void push(ComponentOperator oper);
}
