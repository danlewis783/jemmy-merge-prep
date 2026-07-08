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
package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JCheckBoxMenuItem;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JCheckBoxMenuItemByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JCheckBoxMenuItemOperator extends JMenuItemOperator {
    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JCheckBoxMenuItemOperator(JCheckBoxMenuItem item) {
        super(item);
    }

    public static JCheckBoxMenuItemOperator of(JCheckBoxMenuItem item) {
        return new JCheckBoxMenuItemOperator(item);
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator cont, int index) {
        return new JCheckBoxMenuItemOperator(
                (JCheckBoxMenuItem) waitComponent(cont, PredicatesJ.of(JCheckBoxMenuItem.class), index));
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator cont, String text, StringComparator comparator) {
        return waitFor(cont, text, comparator, 0);
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JCheckBoxMenuItemOperator(
                (JCheckBoxMenuItem) cont.waitSubComponent(PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index));
    }

    public static JCheckBoxMenuItemOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JCheckBoxMenuItemOperator((JCheckBoxMenuItem)
                waitComponent(cont, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator), index));
    }

    public boolean getState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JCheckBoxMenuItem) getSource()).getState()));
    }

    public void setState(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JCheckBoxMenuItem) getSource()).setState(b);

            return null;
        }));
    }
}
