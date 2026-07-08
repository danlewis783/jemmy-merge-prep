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
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SpinnerUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.JSpinnerByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class JSpinnerOperator extends JComponentOperator {
    private @Nullable JButtonOperator decreaseOperator = null;
    private @Nullable JButtonOperator increaseOperator = null;
    private final ScrollDriver driver;

    public JSpinnerOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JSpinnerOperator(JSpinner b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getScrollDriver(getClass());
    }

    public JSpinnerOperator(ContainerOperator cont, int index) {
        this((JSpinner) waitComponent(cont, PredicatesJ.of(JSpinner.class), index));
    }

    public JSpinnerOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JSpinnerOperator(ContainerOperator cont, String text, StringComparator comparator) {
        this(cont, text, comparator, 0);
    }

    public JSpinnerOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JSpinner) cont.waitSubComponent(PredicatesJ.of(JSpinner.class, chooser), index));
    }

    public JSpinnerOperator(ContainerOperator cont, String text, StringComparator comparator, int index) {
        this((JSpinner) waitComponent(cont, new JSpinnerByTextPredicate(text, comparator), index));
    }

    public void scrollTo(ScrollAdjuster adj) {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scroll(JSpinnerOperator.this, adj);

                    return null;
                },
                null,
                TimeoutKey.JSpinnerOperator_WholeScrollTimeout);
    }

    public void scrollToMaximum() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMaximum(JSpinnerOperator.this, SwingConstants.VERTICAL);

                    return null;
                },
                null,
                TimeoutKey.JSpinnerOperator_WholeScrollTimeout);
    }

    public void scrollToMinimum() {
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.scrollToMinimum(JSpinnerOperator.this, SwingConstants.VERTICAL);

                    return null;
                },
                null,
                TimeoutKey.JSpinnerOperator_WholeScrollTimeout);
    }

    public void scrollToObject(Object value, int direction) {
        scrollTo(new ExactScrollAdjuster(this, value, direction));
    }

    public void scrollToString(String pattern, StringComparator comparator, int direction) {
        scrollTo(new ToStringScrollAdjuster(this, pattern, comparator, direction));
    }

    public JButtonOperator getIncreaseOperator() {
        if (increaseOperator == null) {
            increaseOperator = (JButtonOperator)
                    Objects.requireNonNull(createSubOperator(PredicatesJ.of(JButton.class), 0), "no increase button");
        }

        return increaseOperator;
    }

    public JButtonOperator getDecreaseOperator() {
        if (decreaseOperator == null) {
            decreaseOperator = (JButtonOperator)
                    Objects.requireNonNull(createSubOperator(PredicatesJ.of(JButton.class), 1), "no decrease button");
        }

        return decreaseOperator;
    }

    public @Nullable Object getMinimum() {
        SpinnerModel model = getModel();
        if (model instanceof SpinnerNumberModel) {
            return ((SpinnerNumberModel) model).getMinimum();
        } else if (model instanceof SpinnerDateModel) {
            return ((SpinnerDateModel) model).getEnd();
        } else if (model instanceof SpinnerListModel) {
            List<?> list = ((SpinnerListModel) model).getList();

            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    public @Nullable Object getMaximum() {
        SpinnerModel model = getModel();
        if (model instanceof SpinnerNumberModel) {
            return ((SpinnerNumberModel) model).getMaximum();
        } else if (model instanceof SpinnerDateModel) {
            return ((SpinnerDateModel) model).getEnd();
        } else if (model instanceof SpinnerListModel) {
            List<?> list = ((SpinnerListModel) model).getList();

            return list.get(list.size() - 1);
        } else {
            return null;
        }
    }

    public Object getValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getValue()));
    }

    public void setValue(Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).setValue(object);

            return null;
        }));
    }

    public SpinnerUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getUI()));
    }

    public void setUI(SpinnerUI spinnerUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).setUI(spinnerUI);

            return null;
        }));
    }

    public void setModel(SpinnerModel spinnerModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).setModel(spinnerModel);

            return null;
        }));
    }

    public SpinnerModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getModel()));
    }

    public Object getNextValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getNextValue()));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).addChangeListener(changeListener);

            return null;
        }));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).removeChangeListener(changeListener);

            return null;
        }));
    }

    public ChangeListener[] getChangeListeners() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getChangeListeners()));
    }

    public Object getPreviousValue() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getPreviousValue()));
    }

    public void setEditor(JComponent jComponent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).setEditor(jComponent);

            return null;
        }));
    }

    public JComponent getEditor() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JSpinner) getSource()).getEditor()));
    }

    public void commitEdit() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JSpinner) getSource()).commitEdit();

            return null;
        }));
    }

    public static @Nullable JSpinner findJSpinner(Container cont, Predicate<Component> chooser, int index) {
        return (JSpinner) findComponent(cont, PredicatesJ.of(JSpinner.class, chooser), index);
    }

    public static @Nullable JSpinner findJSpinner(Container cont, Predicate<Component> chooser) {
        return findJSpinner(cont, chooser, 0);
    }

    public static @Nullable JSpinner findJSpinner(Container cont, int index) {
        return findJSpinner(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static @Nullable JSpinner findJSpinner(Container cont) {
        return findJSpinner(cont, 0);
    }

    public static JSpinner waitJSpinner(Container cont, Predicate<Component> chooser, int index) {
        return (JSpinner) waitComponent(cont, PredicatesJ.of(JSpinner.class, chooser), index);
    }

    public static JSpinner waitJSpinner(Container cont, Predicate<Component> chooser) {
        return waitJSpinner(cont, chooser, 0);
    }

    public static JSpinner waitJSpinner(Container cont, int index) {
        return waitJSpinner(cont, PredicatesJ.alwaysTrue(), index);
    }

    public static JSpinner waitJSpinner(Container cont) {
        return waitJSpinner(cont, 0);
    }

    public static class DateScrollAdjuster implements ScrollAdjuster {
        private final Date date;
        private final SpinnerDateModel model;

        public DateScrollAdjuster(JSpinnerOperator oper, Date date) {
            if (!(Objects.requireNonNull(oper).getModel() instanceof SpinnerDateModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerDateModel.class.getName());
            }
            model = (SpinnerDateModel) oper.getModel();
            this.date = Objects.requireNonNull(date);
        }

        @Override
        public int getScrollDirection() {
            if (date.after(model.getDate())) {
                return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else if (date.before(model.getDate())) {
                return ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            } else {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.VERTICAL;
        }
    }

    public static class ExactScrollAdjuster extends ObjectScrollAdjuster {
        private final Object obj;

        public ExactScrollAdjuster(JSpinnerOperator oper, Object obj, int direction) {
            super(oper, direction);
            this.obj = obj;
        }

        @Override
        public boolean matches(Object obj) {
            return obj.equals(this.obj);
        }
    }

    public static class ListScrollAdjuster implements ScrollAdjuster {
        private final List<?> elements;
        private int itemIndex;
        private final SpinnerListModel model;

        private ListScrollAdjuster(JSpinnerOperator oper) {
            if (!(Objects.requireNonNull(oper).getModel() instanceof SpinnerListModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerListModel.class.getName());
            }
            model = (SpinnerListModel) oper.getModel();
            elements = model.getList();
        }

        public ListScrollAdjuster(JSpinnerOperator oper, int itemIndex) {
            this(oper);
            this.itemIndex = itemIndex;
        }

        public ListScrollAdjuster(JSpinnerOperator oper, Object value) {
            this(oper);
            this.itemIndex = elements.indexOf(value);
        }

        @Override
        public int getScrollDirection() {
            Object value = model.getValue();
            int curIndex = elements.indexOf(value);
            return Integer.compare(itemIndex, curIndex);
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.VERTICAL;
        }
    }

    public static class NumberScrollAdjuster implements ScrollAdjuster {
        private final SpinnerNumberModel model;
        private final double value;

        public NumberScrollAdjuster(JSpinnerOperator oper, double value) {
            if (Double.isNaN(value)) {
                throw new IllegalArgumentException("value may not be NaN");
            }
            if (Double.isInfinite(value)) {
                throw new IllegalArgumentException("value may not be infinite");
            }

            this.value = value;
            if (!(Objects.requireNonNull(oper).getModel() instanceof SpinnerNumberModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerNumberModel.class.getName());
            }
            model = (SpinnerNumberModel) oper.getModel();
        }

        public NumberScrollAdjuster(JSpinnerOperator oper, Number value) {
            this(oper, value.doubleValue());
        }

        @Override
        public int getScrollDirection() {
            return Double.compare(value, model.getNumber().doubleValue());
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.VERTICAL;
        }
    }

    public abstract static class ObjectScrollAdjuster implements ScrollAdjuster {
        private final int direction;
        private final SpinnerModel model;

        public ObjectScrollAdjuster(JSpinnerOperator oper, int direction) {
            this.direction = direction;
            model = oper.getModel();
        }

        public abstract boolean matches(Object obj);

        @Override
        public int getScrollDirection() {
            if (matches(model.getValue())) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else if (((direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION) && (model.getNextValue() != null))
                    || ((direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION)
                            && (model.getPreviousValue() != null))) {
                return direction;
            } else {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.VERTICAL;
        }
    }

    public static class ToStringScrollAdjuster extends ObjectScrollAdjuster {
        private final StringComparator comparator;
        private final String pattern;

        public ToStringScrollAdjuster(
                JSpinnerOperator oper, String pattern, StringComparator comparator, int direction) {
            super(oper, direction);
            this.pattern = pattern;
            this.comparator = comparator;
        }

        @Override
        public boolean matches(Object obj) {
            String observed = obj.toString();
            return comparator.equals(observed, pattern);
        }
    }
}
