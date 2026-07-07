/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.drivers.windows;

import java.awt.Component;
import java.util.Collections;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.drivers.InternalFrameDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JInternalFrameOperator;

/**
 * Drives internal frames purely through the {@code JInternalFrame} API (setMaximum, setIcon, setClosed, ...), making
 * it immune to look-and-feel differences in the title pane. There is no API to reach the title pane itself, so
 * {@link #getTitlePane(ComponentOperator)} — and therefore the title button getters on
 * {@link JInternalFrameOperator} — are unsupported under this driver. Ported from openjdk/jemmy-v2
 * (CODETOOLS-7902202).
 */
public final class InternalFrameAPIDriver extends LightSupportiveDriver
        implements WindowDriver, FrameDriver, InternalFrameDriver {

    public InternalFrameAPIDriver() {
        super(Collections.singletonList(JInternalFrameOperator.class));
    }

    @Override
    public void activate(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).moveToFront();
        ((JInternalFrameOperator) oper).setSelected(true);
    }

    @Override
    public void maximize(ComponentOperator oper) {
        checkSupported(oper);
        if (!((JInternalFrameOperator) oper).isSelected()) {
            activate(oper);
        }

        ((JInternalFrameOperator) oper).setMaximum(true);
    }

    @Override
    public void demaximize(ComponentOperator oper) {
        checkSupported(oper);
        if (!((JInternalFrameOperator) oper).isSelected()) {
            activate(oper);
        }

        ((JInternalFrameOperator) oper).setMaximum(false);
    }

    @Override
    public void iconify(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).setIcon(true);
    }

    @Override
    public void deiconify(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).setIcon(false);
    }

    @Override
    public void requestClose(ComponentOperator oper) {
        checkSupported(oper);
        ((JInternalFrameOperator) oper).setClosed(true);
    }

    @Override
    public void requestCloseAndThenHide(ComponentOperator oper) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void close(ComponentOperator oper) {
        requestClose(oper);
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
    }

    @Override
    public Component getTitlePane(ComponentOperator oper) {
        throw new UnsupportedOperationException("There is no way to get the title pane of an internal frame.");
    }
}
