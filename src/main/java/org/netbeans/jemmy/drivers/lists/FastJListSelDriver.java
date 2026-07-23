package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JListOperator;

import java.util.Collections;

public final class FastJListSelDriver extends LightSupportiveDriver implements MultiSelListDriver {
    public FastJListSelDriver() {
        super(Collections.singletonList(JListOperator.class));
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        final JListOperator jListOperator = (JListOperator) oper;
        jListOperator.clearSelection();
        jListOperator.setSelectedIndices(indices);
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        final JListOperator jListOperator = (JListOperator) oper;
        jListOperator.setSelectedIndex(index);
    }
}
