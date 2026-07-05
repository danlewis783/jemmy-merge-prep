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

import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.Collections;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.drivers.input.EventDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.WindowOperator;

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

        eventDriver.dispatchEvent(
                oper.getSource(), new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_ACTIVATED));
        eventDriver.dispatchEvent(oper.getSource(), new FocusEvent(oper.getSource(), FocusEvent.FOCUS_GAINED));
    }

    @Override
    public void requestClose(ComponentOperator oper) {
        checkSupported(oper);
        eventDriver.dispatchEvent(
                oper.getSource(), new WindowEvent((Window) oper.getSource(), WindowEvent.WINDOW_CLOSING));
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
        eventDriver.dispatchEvent(
                oper.getSource(), new ComponentEvent(oper.getSource(), ComponentEvent.COMPONENT_RESIZED));
    }
}
