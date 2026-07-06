/*
 * Copyright (c) 1997, 2022, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.drivers.input.RobotDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.Operator;

public final class AppleMenuDriver extends RobotDriver implements MenuDriver {
    public AppleMenuDriver() {
        super(TimeoutKey.Apple_SystemMenuDelay, Collections.singletonList(JMenuBarOperator.class));
    }

    @Override
    public @Nullable MenuElement pushMenu(ComponentOperator oper, List<Predicate<Component>> predicates) {
        long maxTime = Timeouts.get(TimeoutKey.ComponentOperator_WaitComponentTimeout);
        JMenuBar bar = (JMenuBar) oper.getSource();
        activateMenu(bar);
        MenuElement menuObject;
        long startTime = System.currentTimeMillis();
        while (!predicates.get(0).test((Component) (menuObject = getSelectedElement(bar)))) {
            pressKey(KeyEvent.VK_RIGHT, 0);
            releaseKey(KeyEvent.VK_RIGHT, 0);

            if (System.currentTimeMillis() - startTime >= maxTime) {
                throw new TimeoutExpiredException("AppleMenuDriver: can not find an appropriate menu!");
            }
        }

        // the while loop above only exits once the predicate accepted the selected element
        MenuElement selected = Objects.requireNonNull(menuObject, "no menu selected");
        for (int depth = 1; depth < predicates.size(); depth++) {
            int elementIndex = getDesiredElementIndex(selected, predicates, depth);
            if (elementIndex == -1) {
                throw new JemmyException("Unable to find menu (menuitem)");
            }

            for (int i = (depth == 1) ? 0 : 1; i <= elementIndex; i++) {
                pressKey(KeyEvent.VK_DOWN, 0);
                releaseKey(KeyEvent.VK_DOWN, 0);
            }

            if (depth == predicates.size() - 1) {
                pressKey(KeyEvent.VK_ENTER, 0);
                releaseKey(KeyEvent.VK_ENTER, 0);
                return null;
            } else {
                pressKey(KeyEvent.VK_RIGHT, 0);
                releaseKey(KeyEvent.VK_RIGHT, 0);
                selected = selected.getSubElements()[0].getSubElements()[elementIndex];
            }
        }

        return selected;
    }

    private void activateMenu(JMenuBar bar) {
        if (getSelectedElement(bar) == null) {
            tryToActivate();

            if (getSelectedElement(bar) == null) {
                tryToActivate();
            }
        }
    }

    private void tryToActivate() {
        moveMouse(0, 0);
        pressMouse(Operator.getDefaultMouseButton(), 0);
        releaseMouse(Operator.getDefaultMouseButton(), 0);
        pressKey(KeyEvent.VK_RIGHT, 0);
        releaseKey(KeyEvent.VK_RIGHT, 0);
        pressKey(KeyEvent.VK_RIGHT, 0);
        releaseKey(KeyEvent.VK_RIGHT, 0);
    }

    private static @Nullable MenuElement getSelectedElement(MenuElement bar) {
        MenuElement[] subElements = bar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isSelected()) {
                return subElement;
            } else if ((subElement instanceof JMenuItem) && ((JMenuItem) subElement).isSelected()) {
                return subElement;
            }
        }

        return null;
    }

    private static int getDesiredElementIndex(MenuElement bar, List<Predicate<Component>> predicates, int depth) {
        MenuElement[] subElements = bar.getSubElements()[0].getSubElements();
        int realIndex = 0;
        for (MenuElement subElement : subElements) {
            if (subElement instanceof JMenuItem) {
                JMenuItem subMenuItem = (JMenuItem) subElement;
                if (subMenuItem.isVisible() && subMenuItem.isEnabled()) {
                    if (predicates.get(depth).test((Component) subElement)) {
                        return realIndex;
                    }

                    realIndex++;
                }
            }
        }

        return -1;
    }
}
