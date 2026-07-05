
package org.netbeans.jemmy.drivers.input;

import org.netbeans.jemmy.QueueUtils;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;

import java.awt.*;
import java.util.Collections;


public class EventDriver extends LightSupportiveDriver {
    public EventDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ComponentOperator"));
    }

    public void dispatchEvent(Component comp, AWTEvent event) {
        QueueUtils.processEvent(event);
    }
}
