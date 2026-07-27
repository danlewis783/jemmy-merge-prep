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
import javax.swing.JCheckBoxMenuItem;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JCheckBoxMenuItemByLabelPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JCheckBoxMenuItemOperator extends JMenuItemOperator {
    @Override
    public JCheckBoxMenuItem getSource() {
        return (JCheckBoxMenuItem) super.getSource();
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JCheckBoxMenuItem)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(JCheckBoxMenuItem item) {
        super(item);
    }

    public static JCheckBoxMenuItemOperator of(JCheckBoxMenuItem item) {
        return new JCheckBoxMenuItemOperator(item);
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator rootOp, int index) {
        return new JCheckBoxMenuItemOperator(
                (JCheckBoxMenuItem) waitComponent(rootOp, PredicatesJ.of(JCheckBoxMenuItem.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(ContainerOperator rootOp, int index) {
        this((JCheckBoxMenuItem) waitComponent(rootOp, PredicatesJ.of(JCheckBoxMenuItem.class), index));
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JCheckBoxMenuItemOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator comparator) {
        return waitFor(rootOp, text, comparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(ContainerOperator rootOp, String text, StringComparator comparator) {
        this(rootOp, text, comparator, 0);
    }

    public static JCheckBoxMenuItemOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JCheckBoxMenuItemOperator((JCheckBoxMenuItem)
                waitComponent(rootOp, PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JCheckBoxMenuItem)
                waitComponent(rootOp, PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index));
    }

    public static JCheckBoxMenuItemOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        return new JCheckBoxMenuItemOperator((JCheckBoxMenuItem)
                waitComponent(rootOp, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JCheckBoxMenuItemOperator(
            ContainerOperator rootOp, String text, StringComparator stringComparator, int index) {
        this((JCheckBoxMenuItem)
                waitComponent(rootOp, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator), index));
    }

    public static @Nullable JCheckBoxMenuItem findJCheckBoxMenuItem(
            Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBoxMenuItem)
                findComponent(cont, PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index);
    }

    public static @Nullable JCheckBoxMenuItem findJCheckBoxMenuItem(
            Container cont, Predicate<Component> chooser) {
        return findJCheckBoxMenuItem(cont, chooser, 0);
    }

    public static @Nullable JCheckBoxMenuItem findJCheckBoxMenuItem(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJCheckBoxMenuItem(
                cont, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator), index);
    }

    public static @Nullable JCheckBoxMenuItem findJCheckBoxMenuItem(
            Container cont, String text, StringComparator stringComparator) {
        return findJCheckBoxMenuItem(cont, text, stringComparator, 0);
    }

    public static JCheckBoxMenuItem waitJCheckBoxMenuItem(
            Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBoxMenuItem)
                waitComponent(cont, PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index);
    }

    public static JCheckBoxMenuItem waitJCheckBoxMenuItem(
            Container cont, Predicate<Component> chooser) {
        return waitJCheckBoxMenuItem(cont, chooser, 0);
    }

    public static JCheckBoxMenuItem waitJCheckBoxMenuItem(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJCheckBoxMenuItem(
                cont, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator), index);
    }

    public static JCheckBoxMenuItem waitJCheckBoxMenuItem(
            Container cont, String text, StringComparator stringComparator) {
        return waitJCheckBoxMenuItem(cont, text, stringComparator, 0);
    }

    public boolean getState() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getState());
    }

    public void setState(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setState(b));
    }
}
