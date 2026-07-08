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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.drivers.MouseDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JMenuItemOperator;
import org.netbeans.jemmy.operators.JMenuOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;

public class DefaultJMenuDriver extends LightSupportiveDriver implements MenuDriver {
    public DefaultJMenuDriver() {
        super(Collections.unmodifiableList(
                Arrays.asList(JMenuOperator.class, JMenuBarOperator.class, JPopupMenuOperator.class)));
    }

    @Override
    public @Nullable MenuElement pushMenu(ComponentOperator oper, List<Predicate<Component>> predicates) {
        checkSupported(oper);

        if ((oper instanceof JMenuBarOperator) || (oper instanceof JPopupMenuOperator)) {
            JMenuItem item;
            if (oper instanceof JMenuBarOperator) {
                item = waitItem(oper, (JMenuBar) oper.getSource(), predicates, 0);
            } else {
                item = waitItem(oper, (JPopupMenu) oper.getSource(), predicates, 0);
            }

            JMenuItemOperator itemOper;
            if (item instanceof JMenu) {
                itemOper = new JMenuOperator((JMenu) item);
            } else if (item instanceof JMenuItem) {
                itemOper = new JMenuItemOperator(item);
            } else {
                return null;
            }

            return push(
                    itemOper,
                    null,
                    (oper instanceof JMenuBarOperator) ? (JMenuBar) oper.getSource() : null,
                    predicates,
                    1,
                    true);
        } else {
            return push(oper, null, null, predicates, 0, true);
        }
    }

    protected @Nullable MenuElement push(
            ComponentOperator oper,
            @Nullable ComponentOperator lastItem,
            @Nullable JMenuBar menuBar,
            List<Predicate<Component>> predicates,
            int depth,
            boolean pressMouse) {
        oper.waitComponentVisible(true);
        oper.waitComponentEnabled();

        MouseDriver mDriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(oper);
        smartMove(lastItem, oper);

        if (depth > predicates.size() - 1) {
            if ((oper instanceof JMenuOperator) && (menuBar != null) && (getSelectedElement(menuBar) != null)) {
            } else {
                DriverManager.newInstance(JemmyContext.getInstance())
                        .getButtonDriver(oper)
                        .push(oper);
            }

            return (MenuElement) oper.getSource();
        }

        if (pressMouse
                && !((JMenuOperator) oper).isPopupMenuVisible()
                && !((menuBar != null) && (getSelectedElement(menuBar) != null))) {
            DriverManager.newInstance(JemmyContext.getInstance())
                    .getButtonDriver(oper)
                    .push(oper);
        }

        Timeouts.sleep(TimeoutKey.JMenuOperator_WaitBeforePopupTimeout);
        JMenuItem item = waitItem(oper, waitPopupMenu(oper), predicates, depth);
        mDriver.exitMouse(oper);

        if (item instanceof JMenu) {
            JMenuOperator mo = new JMenuOperator((JMenu) item);
            return push(mo, oper, null, predicates, depth + 1, false);
        } else {
            JMenuItemOperator mio = new JMenuItemOperator(item);
            mio.waitComponentEnabled();

            smartMove(oper, mio);
            DriverManager.newInstance(JemmyContext.getInstance())
                    .getButtonDriver(oper)
                    .push(mio);
            return item;
        }
    }

    private void smartMove(@Nullable ComponentOperator last, ComponentOperator oper) {
        if (last == null) {
            oper.enterMouse();
            return;
        }

        long lastXl, lastXr, lastYl, lastYr;
        lastXl = (long) last.getSource().getLocationOnScreen().getX();
        lastXr = lastXl + last.getSource().getWidth();
        lastYl = (long) last.getSource().getLocationOnScreen().getY();
        lastYr = lastYl + last.getSource().getHeight();
        long operXl, operXr, operYl, operYr;
        operXl = (long) oper.getSource().getLocationOnScreen().getX();
        operXr = operXl + oper.getSource().getWidth();
        operYl = (long) oper.getSource().getLocationOnScreen().getY();
        operYr = operYl + oper.getSource().getHeight();
        long overXl, overXr, overYl, overYr;
        overXl = Math.max(lastXl, operXl);
        overXr = Math.min(lastXr, operXr);
        overYl = Math.max(lastYl, operYl);
        overYr = Math.min(lastYr, operYr);

        if (overXl < overXr) {
            last.moveMouse((int) ((overXr - overXl) / 2 - lastXl), last.getCenterY());
            oper.moveMouse((int) ((overXr - overXl) / 2 - operXl), oper.getCenterY());
            oper.enterMouse();
            return;
        }

        if (overYl < overYr) {
            last.moveMouse(last.getCenterX(), (int) ((overYr - overYl) / 2 - lastYl));
            oper.moveMouse(last.getCenterX(), (int) ((overYr - overYl) / 2 - operYl));
            oper.enterMouse();
            return;
        }

        oper.enterMouse();
    }

    protected JPopupMenu waitPopupMenu(ComponentOperator oper) {
        return (JPopupMenu) JPopupMenuOperator.waitJPopupMenu(new IsPopupMenuShowingPredicate(oper))
                .getSource();
    }

    protected JMenuItem waitItem(
            ComponentOperator oper, MenuElement element, List<Predicate<Component>> predicates, int depth) {
        return (JMenuItem) FunctionRepeater.on(new JMenuItemFunction(element, predicates, depth))
                .runUntilNotNull(null);
    }

    private static @Nullable Object getSelectedElement(JMenuBar bar) {
        MenuElement[] subElements = bar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isPopupMenuVisible()) {
                return subElement;
            }
        }

        return null;
    }

    private static class IsPopupMenuShowingPredicate implements Predicate<Component> {
        private final ComponentOperator oper;

        public IsPopupMenuShowingPredicate(ComponentOperator oper) {
            this.oper = oper;
        }

        @Override
        public boolean test(Component comp) {
            return (comp == ((JMenuOperator) oper).getPopupMenu()) && comp.isShowing();
        }
    }

    private static class JMenuItemFunction implements Function<Void, MenuElement> {
        private final List<Predicate<Component>> predicates;
        private final MenuElement cont;
        private final int depth;

        public JMenuItemFunction(MenuElement cont, List<Predicate<Component>> predicates, int depth) {
            this.cont = cont;
            this.predicates = predicates;
            this.depth = depth;
        }

        @Override
        public @Nullable MenuElement apply(Void v) {
            if (!((Component) cont).isShowing()) {
                return null;
            }

            MenuElement[] subElements = cont.getSubElements();
            for (MenuElement subElement : subElements) {
                Component subElementComp = (Component) subElement;
                if (subElementComp.isShowing()
                        && subElementComp.isEnabled()
                        && predicates.get(depth).test(subElementComp)) {
                    return subElement;
                }
            }

            return null;
        }
    }
}
