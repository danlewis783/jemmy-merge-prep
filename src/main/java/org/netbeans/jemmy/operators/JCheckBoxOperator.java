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
import javax.swing.JCheckBox;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JCheckBoxOperator extends JToggleButtonOperator {
    public JCheckBoxOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JCheckBoxOperator(JCheckBox b) {
        super(b);
    }

    public JCheckBoxOperator(ContainerOperator cont, int index) {
        this((JCheckBox) waitComponent(cont, PredicatesJ.of(JCheckBox.class), index));
    }

    public JCheckBoxOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JCheckBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JCheckBoxOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JCheckBox) cont.waitSubComponent(PredicatesJ.of(JCheckBox.class, chooser), index));
    }

    public JCheckBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JCheckBox) waitComponent(
                cont,
                PredicatesJ.of(JCheckBox.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    public static JCheckBox findJCheckBox(Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBox) findJToggleButton(cont, PredicatesJ.of(JCheckBox.class, chooser), index);
    }

    public static JCheckBox findJCheckBox(Container cont, Predicate<Component> chooser) {
        return findJCheckBox(cont, chooser, 0);
    }

    public static JCheckBox findJCheckBox(Container cont, String text, StringComparator stringComparator, int index) {
        return findJCheckBox(
                cont,
                PredicatesJ.of(JCheckBox.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static JCheckBox findJCheckBox(Container cont, String text, StringComparator stringComparator) {
        return findJCheckBox(cont, text, stringComparator, 0);
    }

    public static JCheckBox waitJCheckBox(Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBox) waitJToggleButton(cont, PredicatesJ.of(JCheckBox.class, chooser), index);
    }

    public static JCheckBox waitJCheckBox(Container cont, Predicate<Component> chooser) {
        return waitJCheckBox(cont, chooser, 0);
    }

    public static JCheckBox waitJCheckBox(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJCheckBox(
                cont,
                PredicatesJ.of(JCheckBox.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index);
    }

    public static JCheckBox waitJCheckBox(Container cont, String text, StringComparator stringComparator) {
        return waitJCheckBox(cont, text, stringComparator, 0);
    }
}
