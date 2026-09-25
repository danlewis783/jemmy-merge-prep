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

import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.util.Collections;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.drivers.input.EventDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.WindowOperator;

public final class DefaultWindowDriver extends LightSupportiveDriver implements WindowDriver {
    final EventDriver eventDriver;

    public DefaultWindowDriver() {
        super(Collections.singletonList(WindowOperator.class));
        eventDriver = new EventDriver();
    }

    @Override
    public void activate(ComponentOperator op) {
        checkSupported(op);

        WindowOperator windowOp = (WindowOperator) op;
        if (windowOp.getFocusOwner() == null) {
            windowOp.toFront();
            // toFront activates the window on Windows, but on X11 it only raises it: focus moves
            // at the window manager's discretion, and without one only a focus request moves it
            windowOp.requestFocus();
            // Posting the synthetic events below right away would make Java report the window
            // active before X11 grants the focus; the late native grant then makes the focus
            // manager request focus for this window again, taking it back from whatever window
            // the caller activates next. Only fall back to them when no native focus arrives.
            if (!EventQueue.isDispatchThread() && waitFocused(windowOp)) {
                return;
            }
        }

        eventDriver.dispatchEvent(
                op.getSource(), new WindowEvent((Window) op.getSource(), WindowEvent.WINDOW_ACTIVATED));
        eventDriver.dispatchEvent(op.getSource(), new FocusEvent(op.getSource(), FocusEvent.FOCUS_GAINED));
    }

    /**
     * Polls for the window to be focused for at most {@code WindowOperator_ActivateTimeout}.
     * Deliberately not a {@code Repeater}: a miss here is an expected fallback trigger, not a
     * failure, so it must not pay for (or emit) timeout diagnostics.
     */
    private static boolean waitFocused(WindowOperator windowOp) {
        long startTime = System.currentTimeMillis();
        long budget = Timeouts.get(TimeoutKey.WindowOperator_ActivateTimeout);
        while (!windowOp.isFocused()) {
            if (System.currentTimeMillis() - startTime > budget) {
                return false;
            }

            Timeouts.sleep(TimeoutKey.Waiter_TimeDelta);
        }

        return true;
    }

    @Override
    public void requestClose(ComponentOperator op) {
        checkSupported(op);
        eventDriver.dispatchEvent(
                op.getSource(), new WindowEvent((Window) op.getSource(), WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void requestCloseAndThenHide(ComponentOperator op) {
        requestClose(op);
        op.setVisible(false);
    }

    @Override
    public void close(ComponentOperator op) {
        requestCloseAndThenHide(op);
    }

    @Override
    public void move(ComponentOperator op, int x, int y) {
        checkSupported(op);
        op.setLocation(x, y);
    }

    @Override
    public void resize(ComponentOperator op, int width, int height) {
        checkSupported(op);
        op.setSize(width, height);
        eventDriver.dispatchEvent(
                op.getSource(), new ComponentEvent(op.getSource(), ComponentEvent.COMPONENT_RESIZED));
    }
}
