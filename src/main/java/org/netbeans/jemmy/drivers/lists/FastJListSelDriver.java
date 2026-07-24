package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.QueueTool;
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
    public void selectItems(ComponentOperator op, int[] indices) {
        final JListOperator jListOperator = (JListOperator) op;
        // one EDT hop: clear and re-select atomically so no observer sees the empty selection
        QueueTool.getInstance().runOnQueue(() -> {
            jListOperator.clearSelection();
            jListOperator.setSelectedIndices(indices);
        });
    }

    @Override
    public void selectItem(ComponentOperator op, int index) {
        final JListOperator jListOperator = (JListOperator) op;
        jListOperator.setSelectedIndex(index);
    }
}
