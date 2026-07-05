
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface OrderedListDriver extends MultiSelListDriver {
    public void moveItem(ComponentOperator oper, int itemIndex, int newIndex);
}
