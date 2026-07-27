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
import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.SupplierRepeater;
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
    public @Nullable MenuElement pushMenu(ComponentOperator op, List<Predicate<Component>> predicates) {
        checkSupported(op);

        if ((op instanceof JMenuBarOperator) || (op instanceof JPopupMenuOperator)) {
            JMenuItem item;
            if (op instanceof JMenuBarOperator) {
                item = waitItem(op, (JMenuBar) op.getSource(), predicates, 0);
            } else {
                item = waitItem(op, (JPopupMenu) op.getSource(), predicates, 0);
            }

            JMenuItemOperator itemOper;
            if (item instanceof JMenu) {
                itemOper = JMenuOperator.of((JMenu) item);
            } else if (item instanceof JMenuItem) {
                itemOper = JMenuItemOperator.of(item);
            } else {
                return null;
            }

            return push(
                    itemOper,
                    null,
                    (op instanceof JMenuBarOperator) ? (JMenuBar) op.getSource() : null,
                    predicates,
                    1,
                    true);
        } else {
            return push(op, null, null, predicates, 0, true);
        }
    }

    protected @Nullable MenuElement push(
            ComponentOperator op,
            @Nullable ComponentOperator lastItem,
            @Nullable JMenuBar menuBar,
            List<Predicate<Component>> predicates,
            int depth,
            boolean pressMouse) {
        op.waitComponentVisible(true);
        op.waitComponentEnabled();

        MouseDriver mDriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getMouseDriver(op);
        smartMove(lastItem, op);

        if (depth > predicates.size() - 1) {
            if ((op instanceof JMenuOperator) && (menuBar != null) && (getSelectedElement(menuBar) != null)) {
            } else {
                DriverManager.newInstance(JemmyContext.getInstance())
                        .getButtonDriver(op)
                        .push(op);
            }

            return (MenuElement) op.getSource();
        }

        if (pressMouse
                && !((JMenuOperator) op).isPopupMenuVisible()
                && !((menuBar != null) && (getSelectedElement(menuBar) != null))) {
            DriverManager.newInstance(JemmyContext.getInstance())
                    .getButtonDriver(op)
                    .push(op);
        }

        JMenuItem item = waitItem(op, waitPopupMenu(op), predicates, depth);
        mDriver.exitMouse(op);

        if (item instanceof JMenu) {
            JMenuOperator mo = JMenuOperator.of((JMenu) item);
            return push(mo, op, null, predicates, depth + 1, false);
        } else {
            JMenuItemOperator mio = JMenuItemOperator.of(item);
            mio.waitComponentEnabled();

            smartMove(op, mio);
            DriverManager.newInstance(JemmyContext.getInstance())
                    .getButtonDriver(op)
                    .push(mio);
            return item;
        }
    }

    private void smartMove(@Nullable ComponentOperator last, ComponentOperator op) {
        if (last == null) {
            op.enterMouse();
            return;
        }

        // Single EDT snapshot: the overlap geometry and both target points must come from
        // the same instant, so the whole decision is computed here and only the resulting
        // points are used once we are back on the caller thread.
        SmartMoveTargets targets = QueueTool.getInstance().callOnQueue(() -> {
            long lastXl, lastXr, lastYl, lastYr;
            lastXl = (long) last.getSource().getLocationOnScreen().getX();
            lastXr = lastXl + last.getSource().getWidth();
            lastYl = (long) last.getSource().getLocationOnScreen().getY();
            lastYr = lastYl + last.getSource().getHeight();
            long operXl, operXr, operYl, operYr;
            operXl = (long) op.getSource().getLocationOnScreen().getX();
            operXr = operXl + op.getSource().getWidth();
            operYl = (long) op.getSource().getLocationOnScreen().getY();
            operYr = operYl + op.getSource().getHeight();
            long overXl, overXr, overYl, overYr;
            overXl = Math.max(lastXl, operXl);
            overXr = Math.min(lastXr, operXr);
            overYl = Math.max(lastYl, operYl);
            overYr = Math.min(lastYr, operYr);

            int lastCenterX = last.getSource().getWidth() / 2;
            int lastCenterY = last.getSource().getHeight() / 2;
            int operCenterY = op.getSource().getHeight() / 2;

            if (overXl < overXr) {
                return new SmartMoveTargets(
                        new Point((int) ((overXr - overXl) / 2 - lastXl), lastCenterY),
                        new Point((int) ((overXr - overXl) / 2 - operXl), operCenterY));
            }

            if (overYl < overYr) {
                return new SmartMoveTargets(
                        new Point(lastCenterX, (int) ((overYr - overYl) / 2 - lastYl)),
                        new Point(lastCenterX, (int) ((overYr - overYl) / 2 - operYl)));
            }

            return null;
        });

        if (targets != null) {
            last.moveMouse(targets.lastTarget.x, targets.lastTarget.y);
            op.moveMouse(targets.operTarget.x, targets.operTarget.y);
        }

        op.enterMouse();
    }

    protected JPopupMenu waitPopupMenu(ComponentOperator op) {
        return JPopupMenuOperator.waitFor(new IsPopupMenuShowingPredicate(op)).getSource();
    }

    protected JMenuItem waitItem(
            ComponentOperator op, MenuElement element, List<Predicate<Component>> predicates, int depth) {
        return (JMenuItem) SupplierRepeater.on(new JMenuItemSupplier(element, predicates, depth))
                .runUntilNotNull();
    }

    private static @Nullable Object getSelectedElement(JMenuBar bar) {
        return QueueTool.getInstance().callOnQueue(() -> {
            MenuElement[] subElements = bar.getSubElements();
            for (MenuElement subElement : subElements) {
                if ((subElement instanceof JMenu) && ((JMenu) subElement).isPopupMenuVisible()) {
                    return subElement;
                }
            }

            return null;
        });
    }

    /** The two mouse-move targets {@link #smartMove} computed from one EDT snapshot. */
    private static final class SmartMoveTargets {
        private final Point lastTarget;
        private final Point operTarget;

        SmartMoveTargets(Point lastTarget, Point operTarget) {
            this.lastTarget = lastTarget;
            this.operTarget = operTarget;
        }
    }

    private static class IsPopupMenuShowingPredicate implements Predicate<Component> {
        private final ComponentOperator op;

        public IsPopupMenuShowingPredicate(ComponentOperator op) {
            this.op = op;
        }

        @Override
        public boolean test(Component comp) {
            return (comp == ((JMenuOperator) op).getPopupMenu()) && comp.isShowing();
        }
    }

    private static class JMenuItemSupplier implements Supplier<MenuElement> {
        private final List<Predicate<Component>> predicates;
        private final MenuElement cont;
        private final int depth;

        public JMenuItemSupplier(MenuElement cont, List<Predicate<Component>> predicates, int depth) {
            this.cont = cont;
            this.predicates = predicates;
            this.depth = depth;
        }

        @Override
        public @Nullable MenuElement get() {
            // The whole tree walk plus predicate evaluation runs as one EDT snapshot per
            // poll: this is reached from SupplierRepeater on the test thread, and MenuElement
            // trees are live Swing state that must only be read on the dispatch thread.
            return QueueTool.getInstance().callOnQueue(() -> {
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
            });
        }
    }
}
