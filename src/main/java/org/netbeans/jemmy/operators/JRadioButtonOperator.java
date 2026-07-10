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
import javax.swing.JRadioButton;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparator;

public class JRadioButtonOperator extends JToggleButtonOperator {
    public static JRadioButtonOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    JRadioButtonOperator(JRadioButton b) {
        super(b);
    }

    public static JRadioButtonOperator of(JRadioButton b) {
        return new JRadioButtonOperator(b);
    }

    public static JRadioButtonOperator waitFor(ContainerOperator cont, int index) {
        return new JRadioButtonOperator(
                (JRadioButton) waitComponent(cont, ComponentPredicates.of(JRadioButton.class), index));
    }

    public static JRadioButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static JRadioButtonOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static JRadioButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JRadioButtonOperator(
                (JRadioButton) cont.waitSubComponent(ComponentPredicates.of(JRadioButton.class, chooser), index));
    }

    public static JRadioButtonOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JRadioButtonOperator((JRadioButton) waitComponent(
                cont,
                ComponentPredicates.of(JRadioButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    public static @Nullable JRadioButton findJRadioButton(Container cont, Predicate<Component> chooser, int index) {
        return (JRadioButton) findJToggleButton(cont, ComponentPredicates.of(JRadioButton.class, chooser), index);
    }

    public static @Nullable JRadioButton findJRadioButton(Container cont, Predicate<Component> chooser) {
        return findJRadioButton(cont, chooser, 0);
    }

    public static @Nullable JRadioButton findJRadioButton(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJRadioButton(
                cont,
                ComponentPredicates.of(JRadioButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JRadioButton findJRadioButton(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJRadioButton(cont, text, stringComparator, 0);
    }

    public static JRadioButton waitJRadioButton(Container cont, Predicate<Component> chooser, int index) {
        return (JRadioButton) waitJToggleButton(cont, ComponentPredicates.of(JRadioButton.class, chooser), index);
    }

    public static JRadioButton waitJRadioButton(Container cont, Predicate<Component> chooser) {
        return waitJRadioButton(cont, chooser, 0);
    }

    public static JRadioButton waitJRadioButton(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJRadioButton(
                cont,
                ComponentPredicates.of(JRadioButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static JRadioButton waitJRadioButton(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJRadioButton(cont, text, stringComparator, 0);
    }
}
