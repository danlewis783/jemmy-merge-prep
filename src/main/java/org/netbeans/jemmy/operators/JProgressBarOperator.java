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
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.util.StringComparator;

public class JProgressBarOperator extends JComponentOperator {

    public static JProgressBarOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JProgressBarOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JProgressBar)} instead.
     */
    @Deprecated
    public JProgressBarOperator(JProgressBar b) {
        super(b);
    }

    public static JProgressBarOperator of(JProgressBar b) {
        return new JProgressBarOperator(b);
    }

    public static JProgressBarOperator waitFor(ContainerOperator cont, int index) {
        return new JProgressBarOperator(
                (JProgressBar) waitComponent(cont, ComponentPredicates.of(JProgressBar.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JProgressBarOperator(ContainerOperator cont, int index) {
        this((JProgressBar) waitComponent(cont, ComponentPredicates.of(JProgressBar.class), index));
    }

    public static JProgressBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JProgressBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JProgressBarOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JProgressBarOperator(
                (JProgressBar) cont.waitSubComponent(ComponentPredicates.of(JProgressBar.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JProgressBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JProgressBar) cont.waitSubComponent(ComponentPredicates.of(JProgressBar.class, chooser), index));
    }

    public void waitValue(int value) {
        waitState(new JProgressBarOperatorByValuePredicate(value));
    }

    public void waitValue(String value, StringComparator comparator) {
        waitState(new JProgressBarOperatorByStringPredicate(value, comparator));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).addChangeListener(changeListener));
    }

    public int getMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getMaximum());
    }

    public int getMinimum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getMinimum());
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getModel());
    }

    public int getOrientation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getOrientation());
    }

    public double getPercentComplete() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getPercentComplete());
    }

    public String getString() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getString());
    }

    public ProgressBarUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getUI());
    }

    public int getValue() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).getValue());
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).isBorderPainted());
    }

    public boolean isStringPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((JProgressBar) getSource()).isStringPainted());
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).removeChangeListener(changeListener));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setBorderPainted(b));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setMaximum(i));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setMinimum(i));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setModel(boundedRangeModel));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setOrientation(i));
    }

    public void setString(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setString(string));
    }

    public void setStringPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setStringPainted(b));
    }

    public void setUI(ProgressBarUI progressBarUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setUI(progressBarUI));
    }

    public void setValue(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JProgressBar) getSource()).setValue(i));
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, Predicate<Component> chooser, int index) {
        return (JProgressBar) findComponent(cont, ComponentPredicates.of(JProgressBar.class, chooser), index);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, Predicate<Component> chooser) {
        return findJProgressBar(cont, chooser, 0);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, int index) {
        return findJProgressBar(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont) {
        return findJProgressBar(cont, 0);
    }

    public static JProgressBar waitJProgressBar(Container cont, Predicate<Component> chooser, int index) {
        return (JProgressBar) waitComponent(cont, ComponentPredicates.of(JProgressBar.class, chooser), index);
    }

    public static JProgressBar waitJProgressBar(Container cont, Predicate<Component> chooser) {
        return waitJProgressBar(cont, chooser, 0);
    }

    public static JProgressBar waitJProgressBar(Container cont, int index) {
        return waitJProgressBar(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static JProgressBar waitJProgressBar(Container cont) {
        return waitJProgressBar(cont, 0);
    }

    private static class JProgressBarOperatorByValuePredicate implements Predicate<JProgressBarOperator> {
        private final int value;

        public JProgressBarOperatorByValuePredicate(int value) {
            this.value = value;
        }

        @Override
        public boolean test(JProgressBarOperator jProgressBarOperator) {
            return jProgressBarOperator.getValue() >= value;
        }
    }

    private static class JProgressBarOperatorByStringPredicate implements Predicate<JProgressBarOperator> {
        private final String value;
        private final StringComparator stringComparator;

        public JProgressBarOperatorByStringPredicate(String value, StringComparator stringComparator) {
            this.value = value;
            this.stringComparator = stringComparator;
        }

        @Override
        public boolean test(JProgressBarOperator jProgressBarOperator) {
            return stringComparator.equals(jProgressBarOperator.getString(), value);
        }
    }
}
