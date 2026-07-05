
package org.netbeans.jemmy.drivers.lists;

import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.ListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.EmptyVisualizer;

import javax.swing.*;
import java.util.Collections;

public final class JComboMouseDriver extends LightSupportiveDriver implements ListDriver {
    public JComboMouseDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JComboBoxOperator"));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        JComboBoxOperator coper = (JComboBoxOperator) oper;
        if (!coper.isPopupVisible()) {
            if ("com.sun.java.swing.plaf.motif.MotifLookAndFeel".equals(
                    UIManager.getLookAndFeel().getClass().getName())) {
                oper.clickMouse(oper.getWidth() - 2, oper.getHeight() / 2, 1);
            } else {
                DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(coper.getButton()).push(
                    coper.getButton());
            }
        }

        JListOperator list = new JListOperator(coper.waitList());
        list.setVisualizer(new EmptyVisualizer());
        Timeouts.sleep(TimeoutKey.JComboBoxOperator_BeforeSelectingTimeout);
        DriverManager.newInstance(JemmyProperties.getInstance()).getListDriver(list).selectItem(list, index);
    }
}
