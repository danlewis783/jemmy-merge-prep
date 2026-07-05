
package org.netbeans.jemmy.drivers.windows;

import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.util.Collections;

public final class DefaultInternalFrameDriver extends LightSupportiveDriver
        implements WindowDriver, FrameDriver, InternalFrameDriver {
    public DefaultInternalFrameDriver() {
        super(Collections.singletonList("org.netbeans.jemmy.operators.JInternalFrameOperator"));
    }

    @Override
    public void activate(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).moveToFront();
        ((JInternalFrameOperator) oper).getTitleOperator().clickMouse();
    }

    @Override
    public void requestClose(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).moveToFront();
        ((JInternalFrameOperator) oper).getCloseButton().push();
    }

    @Override
    public void requestCloseAndThenHide(ComponentOperator oper) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(ComponentOperator oper) {
        requestClose(oper);
    }

    @Override
    public void move(ComponentOperator oper, int x, int y) {
        checkSupported(oper);
        ComponentOperator titleOperator = ((JInternalFrameOperator) oper).getTitleOperator();
        titleOperator.dragNDrop(titleOperator.getCenterY(), titleOperator.getCenterY(),
                                x - oper.getX() + titleOperator.getCenterY(),
                                y - oper.getY() + titleOperator.getCenterY());
    }

    @Override
    public void resize(ComponentOperator oper, int width, int height) {
        checkSupported(oper);
        oper.dragNDrop(oper.getWidth() - 1, oper.getHeight() - 1, width - 1, height - 1);
    }

    @Override
    public void iconify(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).getMinimizeButton().clickMouse();
    }

    @Override
    public void deiconify(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).getIconOperator().pushButton();
    }

    @Override
    public void maximize(ComponentOperator oper) {
        checkSupported(oper);

        if (!((JInternalFrameOperator) oper).isMaximum()) {
            if (!((JInternalFrameOperator) oper).isSelected()) {
                activate(oper);
            }

            ((JInternalFrameOperator) oper).getMaximizeButton().push();
        }
    }

    @Override
    public void demaximize(ComponentOperator oper) {
        checkSupported(oper);

        if (((JInternalFrameOperator) oper).isMaximum()) {
            if (!((JInternalFrameOperator) oper).isSelected()) {
                activate(oper);
            }

            ((JInternalFrameOperator) oper).getMaximizeButton().push();
        }
    }

    @Override
    public Component getTitlePane(ComponentOperator operator) {
        ComponentSearcher cs = new ComponentSearcher((Container) operator.getSource());
        return cs.findComponent(PredicatesJ.of(BasicInternalFrameTitlePane.class));
    }
}
