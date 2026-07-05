
package org.netbeans.jemmy.drivers.windows;

import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.drivers.input.EventDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.WindowOperator;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.Collections;

public final class DefaultWindowDriver extends LightSupportiveDriver implements WindowDriver {
    final EventDriver eventDriver;

    public DefaultWindowDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.WindowOperator"));
        eventDriver = new EventDriver();
    }

    @Override
    public void activate(ComponentOperator oper) {
        checkSupported(oper);

        if (((WindowOperator) oper).getFocusOwner() == null) {
            ((WindowOperator) oper).toFront();
        }

        eventDriver.dispatchEvent(oper.getSource(),
                                  new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_ACTIVATED));
        eventDriver.dispatchEvent(oper.getSource(), new FocusEvent(oper.getSource(), FocusEvent.FOCUS_GAINED));
    }

    @Override
    public void requestClose(ComponentOperator oper) {
        checkSupported(oper);
        eventDriver.dispatchEvent(oper.getSource(),
                                  new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void requestCloseAndThenHide(ComponentOperator oper) {
        requestClose(oper);
        oper.setVisible(false);
    }

    @Override
    public void close(ComponentOperator oper) {
        requestCloseAndThenHide(oper);
    }

    @Override
    public void move(ComponentOperator oper, int x, int y) {
        checkSupported(oper);
        oper.setLocation(x, y);
    }

    @Override
    public void resize(ComponentOperator oper, int width, int height) {
        checkSupported(oper);
        oper.setSize(width, height);
        eventDriver.dispatchEvent(oper.getSource(),
                                  new ComponentEvent(oper.getSource(), ComponentEvent.COMPONENT_RESIZED));
    }
}
