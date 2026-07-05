
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface WindowDriver {
    public void activate(ComponentOperator oper);

    public void requestClose(ComponentOperator oper);

    public void requestCloseAndThenHide(ComponentOperator oper);

    @Deprecated
    public void close(ComponentOperator oper);

    public void move(ComponentOperator oper, int x, int y);

    public void resize(ComponentOperator oper, int width, int height);
}
