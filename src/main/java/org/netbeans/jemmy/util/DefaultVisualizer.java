package org.netbeans.jemmy.util;

import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;


public class DefaultVisualizer implements ComponentVisualizer {
    private static final Logger logger = LoggerFactory.getLogger(DefaultVisualizer.class);
    private boolean switchTab = false;
    private boolean scroll = false;
    private boolean modal = false;

    public DefaultVisualizer() {}

    public void checkForModal(boolean modal) {
        this.modal = modal;
    }

    public void scroll(boolean scroll) {
        this.scroll = scroll;
    }

    public void switchTab(boolean switchTab) {
        this.switchTab = switchTab;
    }

    protected boolean isWindowActive(WindowOperator winOp) {
        return winOp.isFocused() && winOp.isActive();
    }

    protected void makeWindowActive(WindowOperator winOp) {
        winOp.activate();
    }

    protected void activate(WindowOperator winOp) {
        boolean active = isWindowActive(winOp);
        winOp.toFront();

        if (!active) {
            makeWindowActive(winOp);
        }
    }

    protected void initInternalFrame(JInternalFrameOperator jifOp) {
        if (!jifOp.isSelected()) {
            jifOp.activate();
        }
    }

    protected void scroll(JScrollPaneOperator jspOp, Component target) {
        if (!jspOp.checkInside(target)) {
            jspOp.scrollToComponent(target);
        }
    }

    protected void switchTab(JTabbedPaneOperator jtpOp, Component target) {
        int tabInd = 0;
        for (int j = 0, maxJ = jtpOp.getTabCount(); j < maxJ; j++) {
            if (target == jtpOp.getComponentAt(j)) {
                tabInd = j;

                break;
            }
        }

        if (jtpOp.getSelectedIndex() != tabInd) {
            jtpOp.selectPage(tabInd);
        }
    }

    @Override
    public void makeVisible(ComponentOperator compOp) {
        try {
            if (modal) {
                Dialog modalDialog = JDialogOperator.getTopModalDialog();
                if ((modalDialog != null) && (compOp.getWindow() != modalDialog)) {
                    throw new JemmyInputException("Component is not on top modal dialog.",
                                                  compOp.getSource());
                }
            }

            WindowOperator winOp = new WindowOperator(compOp.getWindow());
            winOp.setVisualizer(new EmptyVisualizer());
            activate(winOp);

            if (compOp instanceof JInternalFrameOperator) {
                initInternalFrame((JInternalFrameOperator) compOp);
            }

            Container[] conts = compOp.getContainers();
            for (int i = conts.length - 1; i >= 0; i--) {
                Container cont = conts[i];
                if (cont instanceof JInternalFrame) {
                    JInternalFrameOperator jifOp = new JInternalFrameOperator((JInternalFrame) cont);
                    jifOp.setVisualizer(new EmptyVisualizer());
                    initInternalFrame(jifOp);
                } else if (scroll && (cont instanceof JScrollPane)) {
                    JScrollPaneOperator jspOp = new JScrollPaneOperator((JScrollPane) cont);
                    jspOp.setVisualizer(new EmptyVisualizer());
                    scroll(jspOp, compOp.getSource());
                } else if (switchTab && (cont instanceof JTabbedPane)) {
                    JTabbedPaneOperator jtpOp = new JTabbedPaneOperator((JTabbedPane) cont);
                    jtpOp.setVisualizer(new EmptyVisualizer());
                    switchTab(jtpOp, (i == 0) ? compOp.getSource() : conts[i - 1]);
                }
            }
        } catch (TimeoutExpiredException e) {
            logger.warn("timeout making component visible", e);
        }
    }
}
