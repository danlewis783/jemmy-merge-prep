
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface FrameDriver {
    public void iconify(ComponentOperator oper);

    public void deiconify(ComponentOperator oper);

    public void maximize(ComponentOperator oper);

    public void demaximize(ComponentOperator oper);
}
