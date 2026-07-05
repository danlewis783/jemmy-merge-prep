
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ListOperator;

import java.awt.event.KeyEvent;

public final class ListKeyboardDriver extends ListAPIDriver implements MultiSelListDriver {
    public ListKeyboardDriver() {}

    @Override
    public void selectItem(ComponentOperator compOp, int index) {
        ListOperator listOp = (ListOperator) compOp;
        if (listOp.isMultipleMode()) {
            super.selectItem(listOp, index);
        }

        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        FocusDriver focusDriver = driverManager.getFocusDriver(compOp);
        focusDriver.giveFocus(compOp);
        KeyDriver keyDriver = driverManager.getKeyDriver(compOp);
        int current = listOp.getSelectedIndex();
        int diff;
        int key;
        if (index > current) {
            diff = index - current;
            key = KeyEvent.VK_DOWN;
        } else {
            diff = current - index;
            key = KeyEvent.VK_UP;
        }

        for (int i = 0; i < diff; i++) {
            keyDriver.pushKey(compOp, key, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
        }

        keyDriver.pushKey(compOp, KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }
}
