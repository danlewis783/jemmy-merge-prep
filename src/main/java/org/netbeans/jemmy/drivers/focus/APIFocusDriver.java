
package org.netbeans.jemmy.drivers.focus;

import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.input.EventDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

import java.awt.event.FocusEvent;
import java.util.Collections;

public final class APIFocusDriver extends LightSupportiveDriver implements FocusDriver {
    private final EventDriver eventDriver;

    public APIFocusDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.ComponentOperator"));
        eventDriver = new EventDriver();
    }

    @Override
    public void giveFocus(ComponentOperator operator) {
        operator.requestFocus();
        eventDriver.dispatchEvent(operator.getSource(), new FocusEvent(operator.getSource(), FocusEvent.FOCUS_GAINED));
    }
}
