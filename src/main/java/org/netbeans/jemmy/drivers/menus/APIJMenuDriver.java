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
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.AbstractButtonOperator;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;

public final class APIJMenuDriver extends DefaultJMenuDriver implements MenuDriver {

    @Nullable
    private Object push(
            ComponentOperator oper,
            @Nullable JMenuBar menuBar,
            List<Predicate<Component>> predicates,
            int depth,
            boolean pressMouse) {
        oper.waitComponentVisible(true);
        oper.waitComponentEnabled();

        if (depth > predicates.size() - 1) {
            if (oper instanceof JMenuOperator) {
                if (((JMenuOperator) oper).isPopupMenuVisible()) {
                    ((JMenuOperator) oper).setPopupMenuVisible(false);
                }

                ((JMenuOperator) oper).setPopupMenuVisible(true);
                waitPopupMenu(oper);
            }

            ((AbstractButtonOperator) oper).doClick();
            return oper.getSource();
        } else {
            if (((JMenuOperator) oper).isPopupMenuVisible()) {
                ((JMenuOperator) oper).setPopupMenuVisible(false);
            }

            ((JMenuOperator) oper).setPopupMenuVisible(true);
            waitPopupMenu(oper);
        }

        Timeouts.sleep(TimeoutKey.JMenuOperator_WaitBeforePopupTimeout);
        JMenuItem item = waitItem(oper, waitPopupMenu(oper), predicates, depth);
        if (item instanceof JMenu) {
            JMenuOperator mo = new JMenuOperator((JMenu) item);
            Object result = push(mo, null, predicates, depth + 1, false);
            if (result instanceof JMenu) {
                if (!((JMenu) result).isPopupMenuVisible()) {
                    ((JMenuOperator) oper).setPopupMenuVisible(false);
                }
            } else {
                ((JMenuOperator) oper).setPopupMenuVisible(false);
                waitNoPopupMenu(oper);
            }

            return result;
        } else {
            JMenuItemOperator mio = new JMenuItemOperator(item);
            mio.waitComponentEnabled();

            mio.doClick();
            ((JMenuOperator) oper).setPopupMenuVisible(false);
            waitNoPopupMenu(oper);
            return item;
        }
    }

    private void waitNoPopupMenu(ComponentOperator oper) {
        oper.waitState(new JMenuOperatorPopupNotVisible());
    }

    private static class JMenuOperatorPopupNotVisible implements Predicate<JMenuOperator> {
        @Override
        public boolean test(JMenuOperator jMenuOp) {
            return !jMenuOp.isPopupMenuVisible();
        }
    }
}
