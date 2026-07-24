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

package org.netbeans.jemmy.drivers.menus;

import java.awt.Component;
import java.util.List;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

public final class APIJMenuDriver extends DefaultJMenuDriver implements MenuDriver {

    @Nullable
    private Object push(
            ComponentOperator op,
            @Nullable JMenuBar menuBar,
            List<Predicate<Component>> predicates,
            int depth,
            boolean pressMouse) {
        op.waitComponentVisible(true);
        op.waitComponentEnabled();

        if (depth > predicates.size() - 1) {
            if (op instanceof JMenuOperator) {
                if (((JMenuOperator) op).isPopupMenuVisible()) {
                    ((JMenuOperator) op).setPopupMenuVisible(false);
                }

                ((JMenuOperator) op).setPopupMenuVisible(true);
                waitPopupMenu(op);
            }

            ((AbstractButtonOperator) op).doClick();
            return op.getSource();
        } else {
            if (((JMenuOperator) op).isPopupMenuVisible()) {
                ((JMenuOperator) op).setPopupMenuVisible(false);
            }

            ((JMenuOperator) op).setPopupMenuVisible(true);
            waitPopupMenu(op);
        }

        JMenuItem item = waitItem(op, waitPopupMenu(op), predicates, depth);
        if (item instanceof JMenu) {
            JMenuOperator mo = JMenuOperator.of((JMenu) item);
            Object result = push(mo, null, predicates, depth + 1, false);
            if (result instanceof JMenu) {
                boolean popupVisible =
                        QueueTool.getInstance().callOnQueue(() -> ((JMenu) result).isPopupMenuVisible());
                if (!popupVisible) {
                    ((JMenuOperator) op).setPopupMenuVisible(false);
                }
            } else {
                ((JMenuOperator) op).setPopupMenuVisible(false);
                waitNoPopupMenu(op);
            }

            return result;
        } else {
            JMenuItemOperator mio = JMenuItemOperator.of(item);
            mio.waitComponentEnabled();

            mio.doClick();
            ((JMenuOperator) op).setPopupMenuVisible(false);
            waitNoPopupMenu(op);
            return item;
        }
    }

    private void waitNoPopupMenu(ComponentOperator op) {
        op.waitState(new JMenuOperatorPopupNotVisible());
    }

    private static class JMenuOperatorPopupNotVisible implements Predicate<JMenuOperator> {
        @Override
        public boolean test(JMenuOperator jMenuOp) {
            return !jMenuOp.isPopupMenuVisible();
        }
    }
}
