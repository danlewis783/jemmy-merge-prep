/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import org.netbeans.jemmy.JemmyInputException;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.operators.JScrollPaneOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.WindowOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    throw new JemmyInputException("Component is not on top modal dialog.", compOp.getSource());
                }
            }

            WindowOperator winOp = WindowOperator.of(compOp.getWindow());
            winOp.setVisualizer(new EmptyVisualizer());
            activate(winOp);

            if (compOp instanceof JInternalFrameOperator) {
                initInternalFrame((JInternalFrameOperator) compOp);
            }

            Container[] conts = compOp.getContainers();
            for (int i = conts.length - 1; i >= 0; i--) {
                Container cont = conts[i];
                if (cont instanceof JInternalFrame) {
                    JInternalFrameOperator jifOp = JInternalFrameOperator.of((JInternalFrame) cont);
                    jifOp.setVisualizer(new EmptyVisualizer());
                    initInternalFrame(jifOp);
                } else if (scroll && (cont instanceof JScrollPane)) {
                    JScrollPaneOperator jspOp = JScrollPaneOperator.of((JScrollPane) cont);
                    jspOp.setVisualizer(new EmptyVisualizer());
                    scroll(jspOp, compOp.getSource());
                } else if (switchTab && (cont instanceof JTabbedPane)) {
                    JTabbedPaneOperator jtpOp = JTabbedPaneOperator.of((JTabbedPane) cont);
                    jtpOp.setVisualizer(new EmptyVisualizer());
                    switchTab(jtpOp, (i == 0) ? compOp.getSource() : conts[i - 1]);
                }
            }
        } catch (TimeoutExpiredException e) {
            logger.warn("timeout making component visible", e);
        }
    }
}
