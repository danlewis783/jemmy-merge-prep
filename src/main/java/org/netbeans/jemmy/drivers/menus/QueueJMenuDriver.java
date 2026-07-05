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
import javax.swing.MenuElement;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.callables.OneReleaseCallable;
import org.netbeans.jemmy.callables.OneReleaseCallableA;
import org.netbeans.jemmy.callables.OneReleaseCallableB;
import org.netbeans.jemmy.callables.OneReleaseCallableC;
import org.netbeans.jemmy.callables.OneReleaseCallableD;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.MenuDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JMenuBarOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.predicates.PopupMenuPredicate;

public final class QueueJMenuDriver extends LightSupportiveDriver implements MenuDriver {
    public QueueJMenuDriver() {
        super(Collections.unmodifiableList(Arrays.asList(
                "org.netbeans.jemmy.operators.JMenuOperator",
                "org.netbeans.jemmy.operators.JMenuBarOperator",
                "org.netbeans.jemmy.operators.JPopupMenuOperator")));
    }

    @Override
    public MenuElement pushMenu(ComponentOperator componentOperator, List<Predicate<Component>> predicates) {
        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        checkSupported(componentOperator);
        MenuElement result;
        OneReleaseCallable callable;
        if (componentOperator instanceof JMenuBarOperator) {
            callable = new OneReleaseCallableC(predicates, componentOperator, driverManager);
        } else if (componentOperator instanceof JPopupMenuOperator) {
            callable = new OneReleaseCallableB(predicates, componentOperator);
        } else {
            driverManager.getButtonDriver(componentOperator).press(componentOperator);
            callable = new OneReleaseCallableA(predicates, componentOperator);
        }

        result = runOneReleaseCallable(callable, TimeoutKey.ComponentOperator_WaitComponentTimeout);

        if (result instanceof JMenu) {
            for (int i = 1, iMax = predicates.size(); i < iMax; i++) {
                JMenu menu = (JMenu) result;
                Predicate<Component> popupChooser = new PopupMenuPredicate(menu);
                callable = new OneReleaseCallableD(predicates, i, callable, popupChooser);
                result = runOneReleaseCallable(callable, TimeoutKey.JMenuOperator_WaitPopupTimeout);
            }
        }

        return result;
    }

    private MenuElement runOneReleaseCallable(OneReleaseCallable callable, TimeoutKey waitKey) {
        try {
            return FunctionRepeater.on(
                            (Function<Void, MenuElement>)
                                    v -> QueueTool.getInstance().invokeSmoothly(Caller.of(callable)),
                            waitKey,
                            TimeoutKey.QueueJMenuDriver_OneReleaseDelta)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            callable.stop();
        }
    }

    public static boolean isMenuBarSelected(JMenuBar jMenuBar) {
        MenuElement[] subElements = jMenuBar.getSubElements();
        for (MenuElement subElement : subElements) {
            if ((subElement instanceof JMenu) && ((JMenu) subElement).isPopupMenuVisible()) {
                return true;
            }
        }

        return false;
    }
}
