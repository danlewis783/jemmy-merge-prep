
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ListOperator;

import java.util.Collections;


public class ListAPIDriver extends LightSupportiveDriver implements MultiSelListDriver {
    public ListAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ListOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        ListOperator loper = (ListOperator) oper;
        clearSelection(loper);
        loper.select(index);
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        ListOperator loper = (ListOperator) oper;
        clearSelection(loper);

        for (int index : indices) {
            loper.select(index);
        }
    }

    private void clearSelection(ListOperator loper) {
        for (int i = 0, j = loper.getItemCount(); i < j; i++) {
            loper.deselect(i);
        }
    }
}
