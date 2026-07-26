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
import java.util.function.Predicate;
import javax.swing.JRadioButtonMenuItem;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JRadioButtonMenuItemByLabelPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JRadioButtonMenuItemOperator extends JMenuItemOperator {
    @Override
    public JRadioButtonMenuItem getSource() {
        return (JRadioButtonMenuItem) super.getSource();
    }

    public static JRadioButtonMenuItemOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JRadioButtonMenuItem)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(JRadioButtonMenuItem item) {
        super(item);
    }

    public static JRadioButtonMenuItemOperator of(JRadioButtonMenuItem item) {
        return new JRadioButtonMenuItemOperator(item);
    }

    public static JRadioButtonMenuItemOperator waitFor(ContainerOperator rootOp, int index) {
        return new JRadioButtonMenuItemOperator(
                (JRadioButtonMenuItem) waitComponent(rootOp, PredicatesJ.of(JRadioButtonMenuItem.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(ContainerOperator rootOp, int index) {
        this((JRadioButtonMenuItem) waitComponent(rootOp, PredicatesJ.of(JRadioButtonMenuItem.class), index));
    }

    public static JRadioButtonMenuItemOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JRadioButtonMenuItemOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator) {
        return waitFor(rootOp, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(ContainerOperator rootOp, String text, StringComparator stringComparator) {
        this(rootOp, text, stringComparator, 0);
    }

    public static JRadioButtonMenuItemOperator waitFor(
            ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JRadioButtonMenuItemOperator((JRadioButtonMenuItem)
                rootOp.waitSubComponent(PredicatesJ.of(JRadioButtonMenuItem.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JRadioButtonMenuItem)
                rootOp.waitSubComponent(PredicatesJ.of(JRadioButtonMenuItem.class, chooser), index));
    }

    public static JRadioButtonMenuItemOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return new JRadioButtonMenuItemOperator((JRadioButtonMenuItem)
                waitComponent(rootOp, new JRadioButtonMenuItemByLabelPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JRadioButtonMenuItemOperator(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this((JRadioButtonMenuItem)
                waitComponent(rootOp, new JRadioButtonMenuItemByLabelPredicate(text, stringComparator), index));
    }
}
