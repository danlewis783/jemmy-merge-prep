
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.operators.ComponentOperator;

public interface TableDriver {
    public void selectCell(ComponentOperator oper, int row, int column);

    public void editCell(ComponentOperator oper, int row, int column, Object value);
}
