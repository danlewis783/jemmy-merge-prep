
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface MultiSelListDriver extends ListDriver {
    public void selectItems(ComponentOperator oper, int[] indices);
}
