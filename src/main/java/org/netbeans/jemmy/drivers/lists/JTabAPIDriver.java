
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;

import java.util.Collections;
import java.util.concurrent.Callable;

public final class JTabAPIDriver extends LightSupportiveDriver implements ListDriver {
    private final QueueTool queueTool;

    public JTabAPIDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JTabbedPaneOperator"));
        queueTool = QueueTool.getInstance();
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        if (index != -1) {
            queueTool.invokeSmoothly(Caller.of((Callable<Void>) () -> {
                ((JTabbedPaneOperator) oper).setSelectedIndex(index);
                return null;
            }));
        }
    }
}
