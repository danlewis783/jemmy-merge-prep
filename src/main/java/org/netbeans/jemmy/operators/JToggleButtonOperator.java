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
import java.util.function.Predicate;
import javax.swing.JToggleButton;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JToggleButtonOperator extends AbstractButtonOperator {
    public static JToggleButtonOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JToggleButtonOperator(JToggleButton b) {
        super(b);
    }

    public static JToggleButtonOperator of(JToggleButton b) {
        return new JToggleButtonOperator(b);
    }

    public static JToggleButtonOperator waitFor(ContainerOperator cont, int index) {
        return new JToggleButtonOperator(
                (JToggleButton) waitComponent(cont, PredicatesJ.of(JToggleButton.class), index));
    }

    public static JToggleButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JToggleButtonOperator waitFor(
            ContainerOperator cont, StringComparator stringComparator, String text) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JToggleButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JToggleButtonOperator(
                (JToggleButton) cont.waitSubComponent(PredicatesJ.of(JToggleButton.class, chooser), index));
    }

    public static JToggleButtonOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JToggleButtonOperator((JToggleButton) waitComponent(
                cont,
                PredicatesJ.of(JToggleButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    protected void prepareToClick() {
        makeComponentVisible();
    }

    public static @Nullable JToggleButton findJToggleButton(Container cont, Predicate<Component> chooser, int index) {
        return (JToggleButton) findAbstractButton(cont, PredicatesJ.of(JToggleButton.class, chooser), index);
    }

    public static @Nullable JToggleButton findJToggleButton(Container cont, Predicate<Component> chooser) {
        return findJToggleButton(cont, chooser, 0);
    }

    public static @Nullable JToggleButton findJToggleButton(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJToggleButton(
                cont,
                PredicatesJ.of(JToggleButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JToggleButton findJToggleButton(
            Container cont, String text, StringComparator stringComparator) {
        return findJToggleButton(cont, text, stringComparator, 0);
    }

    public static JToggleButton waitJToggleButton(Container cont, Predicate<Component> chooser, int index) {
        return (JToggleButton) waitAbstractButton(cont, PredicatesJ.of(JToggleButton.class, chooser), index);
    }

    public static JToggleButton waitJToggleButton(Container cont, Predicate<Component> chooser) {
        return waitJToggleButton(cont, chooser, 0);
    }

    public static JToggleButton waitJToggleButton(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJToggleButton(
                cont,
                PredicatesJ.of(JToggleButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static JToggleButton waitJToggleButton(Container cont, String text, StringComparator stringComparator) {
        return waitJToggleButton(cont, text, stringComparator, 0);
    }
}
