
package org.netbeans.jemmy.drivers.windows;

import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.input.EventDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.FrameOperator;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Collections;

public final class DefaultFrameDriver extends LightSupportiveDriver implements FrameDriver {
    private final EventDriver eventDriver;

    public DefaultFrameDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.FrameOperator"));
        eventDriver = new EventDriver();
    }

    @Override
    public void iconify(ComponentOperator oper) {
        checkSupported(oper);
        eventDriver.dispatchEvent(oper.getSource(),
                                  new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_ICONIFIED));
        ((FrameOperator) oper).setState(Frame.ICONIFIED);
    }

    @Override
    public void deiconify(ComponentOperator oper) {
        checkSupported(oper);
        eventDriver.dispatchEvent(oper.getSource(),
                                  new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_DEICONIFIED));
        ((FrameOperator) oper).setState(Frame.NORMAL);
    }

    @Override
    public void maximize(ComponentOperator oper) {
        checkSupported(oper);
        oper.setLocation(0, 0);
        Dimension ssize = Toolkit.getDefaultToolkit().getScreenSize();
        oper.setSize(ssize.width, ssize.height);
    }

    @Override
    public void demaximize(ComponentOperator oper) {
        checkSupported(oper);
    }
}
