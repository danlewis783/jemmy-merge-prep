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
import java.awt.Container;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JButton;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JButtonOperator extends AbstractButtonOperator {

    public JButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JButtonOperator(JButton b) {
        super(b);
    }

    public JButtonOperator(ContainerOperator cont, int index) {
        this((JButton) waitComponent(cont, PredicatesJ.of(JButton.class), index));
    }

    public JButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JButton) cont.waitSubComponent(PredicatesJ.of(JButton.class, chooser), index));
    }

    public JButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JButton) waitComponent(
                cont, PredicatesJ.of(JButton.class, new AbstractButtonByTextPredicate(text, stringComparator)), index));
    }

    public boolean isDefaultButton() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JButton) getSource()).isDefaultButton()));
    }

    public boolean isDefaultCapable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JButton) getSource()).isDefaultCapable()));
    }

    public void setDefaultCapable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JButton) getSource()).setDefaultCapable(b);

            return null;
        }));
    }

    protected void prepareToClick() {
        makeComponentVisible();
    }

    public static @Nullable JButton findJButton(Container cont, Predicate<Component> chooser, int index) {
        return (JButton) findAbstractButton(cont, PredicatesJ.of(JButton.class, chooser), index);
    }

    public static @Nullable JButton findJButton(Container cont, Predicate<Component> chooser) {
        return findJButton(cont, chooser, 0);
    }

    public static @Nullable JButton findJButton(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJButton(
                cont, PredicatesJ.of(JButton.class, new AbstractButtonByTextPredicate(text, stringComparator)), index);
    }

    public static @Nullable JButton findJButton(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJButton(cont, text, stringComparator, 0);
    }

    public static JButton waitJButton(Container cont, Predicate<Component> chooser, int index) {
        return (JButton) waitAbstractButton(cont, PredicatesJ.of(JButton.class, chooser), index);
    }

    public static JButton waitJButton(Container cont, Predicate<Component> chooser) {
        return waitJButton(cont, chooser, 0);
    }

    public static JButton waitJButton(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJButton(
                cont, PredicatesJ.of(JButton.class, new AbstractButtonByTextPredicate(text, stringComparator)), index);
    }

    public static JButton waitJButton(Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJButton(cont, text, stringComparator, 0);
    }
}
