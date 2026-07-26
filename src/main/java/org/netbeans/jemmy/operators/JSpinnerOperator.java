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
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.JSpinnerByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class JSpinnerOperator extends JComponentOperator {
    @Override
    public JSpinner getSource() {
        return (JSpinner) super.getSource();
    }

    private @Nullable JButtonOperator decreaseOperator = null;
    private @Nullable JButtonOperator increaseOperator = null;

    public static JSpinnerOperator waitFor(ContainerOperator rootOp) {
        return waitFor(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp) {
        this(rootOp, 0);
    }

    /**
     * @deprecated Use {@link #of(JSpinner)} instead.
     */
    @Deprecated
    public JSpinnerOperator(JSpinner b) {
        super(b);
    }

    private ScrollDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static JSpinnerOperator of(JSpinner b) {
        return new JSpinnerOperator(b);
    }

    public static JSpinnerOperator waitFor(ContainerOperator rootOp, int index) {
        return new JSpinnerOperator((JSpinner) waitComponent(rootOp, PredicatesJ.of(JSpinner.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp, int index) {
        this((JSpinner) waitComponent(rootOp, PredicatesJ.of(JSpinner.class), index));
    }

    public static JSpinnerOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser) {
        return waitFor(rootOp, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp, Predicate<Component> chooser) {
        this(rootOp, chooser, 0);
    }

    public static JSpinnerOperator waitFor(ContainerOperator rootOp, String text, StringComparator comparator) {
        return waitFor(rootOp, text, comparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp, String text, StringComparator comparator) {
        this(rootOp, text, comparator, 0);
    }

    public static JSpinnerOperator waitFor(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        return new JSpinnerOperator(
                (JSpinner) rootOp.waitSubComponent(PredicatesJ.of(JSpinner.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp, Predicate<Component> chooser, int index) {
        this((JSpinner) rootOp.waitSubComponent(PredicatesJ.of(JSpinner.class, chooser), index));
    }

    public static JSpinnerOperator waitFor(
            ContainerOperator rootOp, String text, StringComparator comparator, int index) {
        return new JSpinnerOperator(
                (JSpinner) waitComponent(rootOp, new JSpinnerByTextPredicate(text, comparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JSpinnerOperator(ContainerOperator rootOp, String text, StringComparator comparator, int index) {
        this((JSpinner) waitComponent(rootOp, new JSpinnerByTextPredicate(text, comparator), index));
    }

    public void scrollTo(ScrollAdjuster adj) {
        runTimeRestricted(
                () -> driver().scroll(JSpinnerOperator.this, adj), TimeoutKey.JSpinnerOperator_WholeScrollTimeout);
    }

    public void scrollToMaximum() {
        runTimeRestricted(
                () -> driver().scrollToMaximum(JSpinnerOperator.this, SwingConstants.VERTICAL),
                TimeoutKey.JSpinnerOperator_WholeScrollTimeout);
    }

    public void scrollToMinimum() {
        runTimeRestricted(
                () -> driver().scrollToMinimum(JSpinnerOperator.this, SwingConstants.VERTICAL),
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
            increaseOperator = JButtonOperator.of((JButton) waitSubComponent(PredicatesJ.of(JButton.class), 0));
        }

        return increaseOperator;
    }

    public JButtonOperator getDecreaseOperator() {
        if (decreaseOperator == null) {
            decreaseOperator = JButtonOperator.of((JButton) waitSubComponent(PredicatesJ.of(JButton.class), 1));
        }

        return decreaseOperator;
    }

    public @Nullable Object getMinimum() {
        return QueueTool.getInstance().callOnQueue(() -> {
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
        });
    }

    public @Nullable Object getMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> {
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
        });
    }

    public Object getValue() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getValue());
    }

    public void setValue(Object object) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setValue(object));
    }

    public SpinnerUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getUI());
    }

    public void setUI(SpinnerUI spinnerUI) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setUI(spinnerUI));
    }

    public void setModel(SpinnerModel spinnerModel) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setModel(spinnerModel));
    }

    public SpinnerModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getModel());
    }

    public Object getNextValue() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getNextValue());
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().addChangeListener(changeListener));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> getSource().removeChangeListener(changeListener));
    }

    public ChangeListener[] getChangeListeners() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getChangeListeners());
    }

    public Object getPreviousValue() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getPreviousValue());
    }

    public void setEditor(JComponent jComponent) {
        QueueTool.getInstance().runOnQueue(() -> getSource().setEditor(jComponent));
    }

    public JComponent getEditor() {
        return QueueTool.getInstance().callOnQueue(() -> getSource().getEditor());
    }

    public void commitEdit() {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                getSource().commitEdit();
            } catch (ParseException e) {
                throw new JemmyException("Exception when committing edit", e);
            }
        });
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

        public DateScrollAdjuster(JSpinnerOperator op, Date date) {
            if (!(Objects.requireNonNull(op).getModel() instanceof SpinnerDateModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerDateModel.class.getName());
            }
            model = (SpinnerDateModel) op.getModel();
            this.date = Objects.requireNonNull(date);
        }

        @Override
        public int getScrollDirection() {
            Date modelDate = QueueTool.getInstance().callOnQueue(model::getDate);
            if (date.after(modelDate)) {
                return ScrollAdjuster.INCREASE_SCROLL_DIRECTION;
            } else if (date.before(modelDate)) {
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

        public ExactScrollAdjuster(JSpinnerOperator op, Object obj, int direction) {
            super(op, direction);
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

        private ListScrollAdjuster(JSpinnerOperator op) {
            if (!(Objects.requireNonNull(op).getModel() instanceof SpinnerListModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerListModel.class.getName());
            }
            model = (SpinnerListModel) op.getModel();
            elements = QueueTool.getInstance().callOnQueue(model::getList);
        }

        public ListScrollAdjuster(JSpinnerOperator op, int itemIndex) {
            this(op);
            this.itemIndex = itemIndex;
        }

        public ListScrollAdjuster(JSpinnerOperator op, Object value) {
            this(op);
            this.itemIndex = elements.indexOf(value);
        }

        @Override
        public int getScrollDirection() {
            Object value = QueueTool.getInstance().callOnQueue(model::getValue);
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

        public NumberScrollAdjuster(JSpinnerOperator op, double value) {
            if (Double.isNaN(value)) {
                throw new IllegalArgumentException("value may not be NaN");
            }
            if (Double.isInfinite(value)) {
                throw new IllegalArgumentException("value may not be infinite");
            }

            this.value = value;
            if (!(Objects.requireNonNull(op).getModel() instanceof SpinnerNumberModel)) {
                throw new IllegalArgumentException("JSpinner model is not a " + SpinnerNumberModel.class.getName());
            }
            model = (SpinnerNumberModel) op.getModel();
        }

        public NumberScrollAdjuster(JSpinnerOperator op, Number value) {
            this(op, value.doubleValue());
        }

        @Override
        public int getScrollDirection() {
            Number modelNumber = QueueTool.getInstance().callOnQueue(model::getNumber);
            return Double.compare(value, modelNumber.doubleValue());
        }

        @Override
        public int getScrollOrientation() {
            return SwingConstants.VERTICAL;
        }
    }

    public abstract static class ObjectScrollAdjuster implements ScrollAdjuster {
        private final int direction;
        private final SpinnerModel model;

        public ObjectScrollAdjuster(JSpinnerOperator op, int direction) {
            this.direction = direction;
            model = op.getModel();
        }

        public abstract boolean matches(Object obj);

        @Override
        public int getScrollDirection() {
            return QueueTool.getInstance().callOnQueue(() -> {
                if (matches(model.getValue())) {
                    return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
                } else if (((direction == ScrollAdjuster.INCREASE_SCROLL_DIRECTION)
                                && (model.getNextValue() != null))
                        || ((direction == ScrollAdjuster.DECREASE_SCROLL_DIRECTION)
                                && (model.getPreviousValue() != null))) {
                    return direction;
                } else {
                    return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
                }
            });
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
                JSpinnerOperator op, String pattern, StringComparator comparator, int direction) {
            super(op, direction);
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
