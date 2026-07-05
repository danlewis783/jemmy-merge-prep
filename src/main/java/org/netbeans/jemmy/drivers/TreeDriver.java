
package org.netbeans.jemmy.drivers;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;

public interface TreeDriver extends MultiSelListDriver {
    public void expandItem(ComponentOperator oper, int index);

    public void collapseItem(ComponentOperator oper, int index);

    public void editItem(ComponentOperator oper, int index, Object newValue, TimeoutKey waitEditorTime);

    public void startEditing(ComponentOperator oper, int index, TimeoutKey waitEditorTime);
}
