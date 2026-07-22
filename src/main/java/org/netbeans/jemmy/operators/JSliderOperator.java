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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Predicate;
import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.ScrollDriver;
import org.netbeans.jemmy.drivers.scrolling.ScrollAdjuster;
import org.netbeans.jemmy.predicates.ComponentPredicates;

public class JSliderOperator extends JComponentOperator {
    @Deprecated
    public static final int CLICK_SCROLL_MODEL = 1;

    @Deprecated
    public static final int PUSH_AND_WAIT_SCROLL_MODEL = 2;

    private int scrollModel = CLICK_SCROLL_MODEL;

    public static JSliderOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JSliderOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JSlider)} instead.
     */
    @Deprecated
    public JSliderOperator(JSlider b) {
        super(b);
    }

    private ScrollDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getScrollDriver(getClass());
    }

    public static JSliderOperator of(JSlider b) {
        return new JSliderOperator(b);
    }

    public static JSliderOperator waitFor(ContainerOperator cont, int index) {
        return new JSliderOperator((JSlider) waitComponent(cont, ComponentPredicates.of(JSlider.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JSliderOperator(ContainerOperator cont, int index) {
        this((JSlider) waitComponent(cont, ComponentPredicates.of(JSlider.class), index));
    }

    public static JSliderOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JSliderOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JSliderOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JSliderOperator(
                (JSlider) cont.waitSubComponent(ComponentPredicates.of(JSlider.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JSliderOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JSlider) cont.waitSubComponent(ComponentPredicates.of(JSlider.class, chooser), index));
    }

    @Deprecated
    public void setScrollModel(int model) {
        scrollModel = model;
    }

    @Deprecated
    public int getScrollModel() {
        return scrollModel;
    }

    public void scrollTo(ScrollAdjuster adj) {
        makeComponentVisible();
        runTimeRestricted(
                () -> driver().scroll(JSliderOperator.this, adj),
                TimeoutKey.JSliderOperator_WholeScrollTimeout);
    }

    public void scrollToValue(int value) {
        scrollTo(new ValueScrollAdjuster(value));
    }

    public void scrollToMaximum() {
        scrollToValue(getMaximum());
    }

    public void scrollToMinimum() {
        scrollToValue(getMinimum());
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).addChangeListener(changeListener));
    }

    public Hashtable<?, ?> createStandardLabels(int i) {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).createStandardLabels(i));
    }

    public Hashtable<?, ?> createStandardLabels(int i, int i1) {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).createStandardLabels(i, i1));
    }

    public int getExtent() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getExtent());
    }

    public boolean getInverted() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getInverted());
    }

    public Dictionary<?, ?> getLabelTable() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getLabelTable());
    }

    public int getMajorTickSpacing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getMajorTickSpacing());
    }

    public int getMaximum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getMaximum());
    }

    public int getMinimum() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getMinimum());
    }

    public int getMinorTickSpacing() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getMinorTickSpacing());
    }

    public BoundedRangeModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getModel());
    }

    public int getOrientation() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getOrientation());
    }

    public boolean getPaintLabels() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getPaintLabels());
    }

    public boolean getPaintTicks() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getPaintTicks());
    }

    public boolean getPaintTrack() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getPaintTrack());
    }

    public boolean getSnapToTicks() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getSnapToTicks());
    }

    public SliderUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getUI());
    }

    public int getValue() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getValue());
    }

    public boolean getValueIsAdjusting() {
        return QueueTool.getInstance().callOnQueue(() -> ((JSlider) getSource()).getValueIsAdjusting());
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).removeChangeListener(changeListener));
    }

    public void setExtent(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setExtent(i));
    }

    public void setInverted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setInverted(b));
    }

    public void setLabelTable(Dictionary<?, ?> dictionary) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setLabelTable(dictionary));
    }

    public void setMajorTickSpacing(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setMajorTickSpacing(i));
    }

    public void setMaximum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setMaximum(i));
    }

    public void setMinimum(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setMinimum(i));
    }

    public void setMinorTickSpacing(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setMinorTickSpacing(i));
    }

    public void setModel(BoundedRangeModel boundedRangeModel) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setModel(boundedRangeModel));
    }

    public void setOrientation(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setOrientation(i));
    }

    public void setPaintLabels(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setPaintLabels(b));
    }

    public void setPaintTicks(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setPaintTicks(b));
    }

    public void setPaintTrack(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setPaintTrack(b));
    }

    public void setSnapToTicks(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setSnapToTicks(b));
    }

    public void setUI(SliderUI sliderUI) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setUI(sliderUI));
    }

    public void setValue(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setValue(i));
    }

    public void setValueIsAdjusting(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((JSlider) getSource()).setValueIsAdjusting(b));
    }

    public static @Nullable JSlider findJSlider(Container cont, Predicate<Component> chooser, int index) {
        return (JSlider) findComponent(cont, ComponentPredicates.of(JSlider.class, chooser), index);
    }

    public static @Nullable JSlider findJSlider(Container cont, Predicate<Component> chooser) {
        return findJSlider(cont, chooser, 0);
    }

    public static @Nullable JSlider findJSlider(Container cont, int index) {
        return findJSlider(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static @Nullable JSlider findJSlider(Container cont) {
        return findJSlider(cont, 0);
    }

    public static JSlider waitJSlider(Container cont, Predicate<Component> chooser, int index) {
        return (JSlider) waitComponent(cont, ComponentPredicates.of(JSlider.class, chooser), index);
    }

    public static JSlider waitJSlider(Container cont, Predicate<Component> chooser) {
        return waitJSlider(cont, chooser, 0);
    }

    public static JSlider waitJSlider(Container cont, int index) {
        return waitJSlider(cont, ComponentPredicates.alwaysTrue(), index);
    }

    public static JSlider waitJSlider(Container cont) {
        return waitJSlider(cont, 0);
    }

    private class ValueScrollAdjuster implements ScrollAdjuster {
        final int value;

        public ValueScrollAdjuster(int value) {
            this.value = value;
        }

        @Override
        public int getScrollDirection() {
            if (getValue() == value) {
                return ScrollAdjuster.DO_NOT_TOUCH_SCROLL_DIRECTION;
            } else {
                return (getValue() < value)
                        ? ScrollAdjuster.INCREASE_SCROLL_DIRECTION
                        : ScrollAdjuster.DECREASE_SCROLL_DIRECTION;
            }
        }

        @Override
        public int getScrollOrientation() {
            return getOrientation();
        }
    }
}
