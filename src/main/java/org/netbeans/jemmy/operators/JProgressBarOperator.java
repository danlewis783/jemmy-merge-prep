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
import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ProgressBarUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JProgressBarOperator extends JComponentOperator {
    public static final String MAXIMUM_DPROP = "Maximum";
    public static final String MINIMUM_DPROP = "Minimum";
    public static final String VALUE_DPROP = "Value";
    private static final Logger logger = LoggerFactory.getLogger(JProgressBarOperator.class);

    public JProgressBarOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JProgressBarOperator(JProgressBar b) {
        super(b);
    }

    public JProgressBarOperator(ContainerOperator cont, int index) {
        this((JProgressBar) waitComponent(cont, PredicatesJ.of(JProgressBar.class), index));
    }

    public JProgressBarOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JProgressBarOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JProgressBar) cont.waitSubComponent(PredicatesJ.of(JProgressBar.class, chooser), index));
    }

    public void waitValue(int value) {
        waitState(new JProgressBarOperatorByValuePredicate(value));
    }

    public void waitValue(String value, StringComparator comparator) {
        waitState(new JProgressBarOperatorByStringPredicate(value, comparator));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).addChangeListener(changeListener);

            return null;
        }));
    }

    public int getMaximum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getMaximum()));
    }

    public int getMinimum() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getMinimum()));
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getModel()));
    }

    public int getOrientation() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getOrientation()));
    }

    public double getPercentComplete() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getPercentComplete()));
    }

    public String getString() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getString()));
    }

    public ProgressBarUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getUI()));
    }

    public int getValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).getValue()));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).isBorderPainted()));
    }

    public boolean isStringPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JProgressBar) getSource()).isStringPainted()));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).removeChangeListener(changeListener);

            return null;
        }));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setBorderPainted(b);

            return null;
        }));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setMaximum(i);

            return null;
        }));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setMinimum(i);

            return null;
        }));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setModel(boundedRangeModel);

            return null;
        }));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setOrientation(i);

            return null;
        }));
    }

    public void setString(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setString(string);

            return null;
        }));
    }

    public void setStringPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setStringPainted(b);

            return null;
        }));
    }

    public void setUI(ProgressBarUI progressBarUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setUI(progressBarUI);

            return null;
        }));
    }

    public void setValue(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JProgressBar) getSource()).setValue(i);

            return null;
        }));
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, Predicate<Component> chooser, int index) {
        return (JProgressBar) findComponent(cont, PredicatesJ.of(JProgressBar.class, chooser), index);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, Predicate<Component> chooser) {
        return findJProgressBar(cont, chooser, 0);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont, int index) {
        return findJProgressBar(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JProgressBar findJProgressBar(Container cont) {
        return findJProgressBar(cont, 0);
    }

    public static JProgressBar waitJProgressBar(Container cont, Predicate<Component> chooser, int index) {
        return (JProgressBar) waitComponent(cont, PredicatesJ.of(JProgressBar.class, chooser), index);
    }

    public static JProgressBar waitJProgressBar(Container cont, Predicate<Component> chooser) {
        return waitJProgressBar(cont, chooser, 0);
    }

    public static JProgressBar waitJProgressBar(Container cont, int index) {
        return waitJProgressBar(cont, PredicatesJ.alwaysTrue(), index);
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

    private class JProgressBarOperatorByStringPredicate implements Predicate<JProgressBarOperator> {
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
