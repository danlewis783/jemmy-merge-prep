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

package org.netbeans.jemmy.drivers.windows;

import java.awt.Component;
import java.awt.Container;
import java.util.Collections;
import java.util.Objects;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;

public final class DefaultInternalFrameDriver extends LightSupportiveDriver
        implements WindowDriver, FrameDriver, InternalFrameDriver {
    public DefaultInternalFrameDriver() {
        super(Collections.singletonList(JInternalFrameOperator.class));
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
        titleOperator.dragNDrop(
                titleOperator.getCenterY(),
                titleOperator.getCenterY(),
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
        return Objects.requireNonNull(
                cs.findComponent(PredicatesJ.of(BasicInternalFrameTitlePane.class)), "title pane not found");
    }
}
