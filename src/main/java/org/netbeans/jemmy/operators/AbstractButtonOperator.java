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
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.function.Predicate;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.AbstractButtonOperatorByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class AbstractButtonOperator extends JComponentOperator {
    /**
     * @deprecated Use {@link #of(AbstractButton)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(AbstractButton b) {
        super(b);
    }

    private ButtonDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getButtonDriver(getClass());
    }

    public static AbstractButtonOperator of(AbstractButton b) {
        return new AbstractButtonOperator(b);
    }

    public static AbstractButtonOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public static AbstractButtonOperator waitFor(ContainerOperator cont, int index) {
        return new AbstractButtonOperator(
                (AbstractButton) waitComponent(cont, PredicatesJ.of(AbstractButton.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont, int index) {
        this((AbstractButton) waitComponent(cont, PredicatesJ.of(AbstractButton.class), index));
    }

    public static AbstractButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static AbstractButtonOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static AbstractButtonOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new AbstractButtonOperator(
                (AbstractButton) cont.waitSubComponent(PredicatesJ.of(AbstractButton.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((AbstractButton) cont.waitSubComponent(PredicatesJ.of(AbstractButton.class, chooser), index));
    }

    public static AbstractButtonOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new AbstractButtonOperator((AbstractButton) waitComponent(
                cont,
                PredicatesJ.of(AbstractButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public AbstractButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((AbstractButton) waitComponent(
                cont,
                PredicatesJ.of(AbstractButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    public void push() {
        makeComponentVisible();

        waitComponentEnabled();

        driver().push(this);
    }

    public void pushNoBlock() {
        runNoBlocking(this::push);
    }

    public void changeSelection(boolean selected) {
        if (isSelected() != selected) {
            push();
        }

        if (getVerification()) {
            waitSelected(selected);
        }
    }

    public void changeSelectionNoBlock(boolean selected) {
        runNoBlocking(() -> changeSelection(selected));
    }

    public void press() {
        makeComponentVisible();

        waitComponentEnabled();

        driver().press(this);
    }

    public void release() {
        waitComponentEnabled();

        driver().release(this);
    }

    public void waitSelected(boolean selected) {
        waitState(new AbstractButtonOperatorSelectedPredicate<>(selected));
    }

    public void waitText(String text, StringComparator stringComparator) {
        waitState(new AbstractButtonOperatorByTextPredicate<>(text, stringComparator));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).addActionListener(actionListener));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).addChangeListener(changeListener));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).addItemListener(itemListener));
    }

    public void doClick() {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).doClick());
    }

    public void doClick(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).doClick(i));
    }

    public String getActionCommand() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getActionCommand());
    }

    public @Nullable Icon getDisabledIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getDisabledIcon());
    }

    public Icon getDisabledSelectedIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getDisabledSelectedIcon());
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getHorizontalAlignment());
    }

    public int getHorizontalTextPosition() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getHorizontalTextPosition());
    }

    public @Nullable Icon getIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getIcon());
    }

    public Insets getMargin() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getMargin());
    }

    public int getMnemonic() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getMnemonic());
    }

    public ButtonModel getModel() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getModel());
    }

    public Icon getPressedIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getPressedIcon());
    }

    public Icon getRolloverIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getRolloverIcon());
    }

    public Icon getRolloverSelectedIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getRolloverSelectedIcon());
    }

    public Icon getSelectedIcon() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getSelectedIcon());
    }

    public Object[] getSelectedObjects() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getSelectedObjects());
    }

    public String getText() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getText());
    }

    public ButtonUI getUI() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getUI());
    }

    public int getVerticalAlignment() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getVerticalAlignment());
    }

    public int getVerticalTextPosition() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).getVerticalTextPosition());
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).isBorderPainted());
    }

    public boolean isContentAreaFilled() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).isContentAreaFilled());
    }

    public boolean isFocusPainted() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).isFocusPainted());
    }

    public boolean isRolloverEnabled() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).isRolloverEnabled());
    }

    public boolean isSelected() {
        return QueueTool.getInstance().callOnQueue(() -> ((AbstractButton) getSource()).isSelected());
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).removeActionListener(actionListener));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).removeChangeListener(changeListener));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).removeItemListener(itemListener));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setActionCommand(string));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setBorderPainted(b));
    }

    public void setContentAreaFilled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setContentAreaFilled(b));
    }

    public void setDisabledIcon(@Nullable Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setDisabledIcon(icon));
    }

    public void setDisabledSelectedIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setDisabledSelectedIcon(icon));
    }

    public void setFocusPainted(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setFocusPainted(b));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setHorizontalAlignment(i));
    }

    public void setHorizontalTextPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setHorizontalTextPosition(i));
    }

    public void setIcon(@Nullable Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setIcon(icon));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setMargin(insets));
    }

    public void setMnemonic(char c) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setMnemonic(c));
    }

    public void setMnemonic(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setMnemonic(i));
    }

    public void setModel(ButtonModel buttonModel) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setModel(buttonModel));
    }

    public void setPressedIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setPressedIcon(icon));
    }

    public void setRolloverEnabled(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setRolloverEnabled(b));
    }

    public void setRolloverIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setRolloverIcon(icon));
    }

    public void setRolloverSelectedIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setRolloverSelectedIcon(icon));
    }

    public void setSelected(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setSelected(b));
    }

    public void setSelectedIcon(Icon icon) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setSelectedIcon(icon));
    }

    public void setText(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setText(string));
    }

    public void setUI(ButtonUI buttonUI) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setUI(buttonUI));
    }

    public void setVerticalAlignment(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setVerticalAlignment(i));
    }

    public void setVerticalTextPosition(int i) {
        QueueTool.getInstance().runOnQueue(() -> ((AbstractButton) getSource()).setVerticalTextPosition(i));
    }

    public static @Nullable AbstractButton findAbstractButton(Container cont, Predicate<Component> chooser, int index) {
        return (AbstractButton) findComponent(cont, PredicatesJ.of(AbstractButton.class, chooser), index);
    }

    public static @Nullable AbstractButton findAbstractButton(Container cont, Predicate<Component> chooser) {
        return findAbstractButton(cont, chooser, 0);
    }

    public static @Nullable AbstractButton findAbstractButton(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findAbstractButton(cont, new AbstractButtonByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable AbstractButton findAbstractButton(
            Container cont, String text, StringComparator stringComparator) {
        return findAbstractButton(cont, text, stringComparator, 0);
    }

    public static AbstractButton waitAbstractButton(Container cont, Predicate<Component> chooser, int index) {
        return (AbstractButton) waitComponent(cont, PredicatesJ.of(AbstractButton.class, chooser), index);
    }

    public static AbstractButton waitAbstractButton(Container cont, Predicate<Component> chooser) {
        return waitAbstractButton(cont, chooser, 0);
    }

    public static AbstractButton waitAbstractButton(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitAbstractButton(cont, new AbstractButtonByTextPredicate(text, stringComparator), index);
    }

    public static AbstractButton waitAbstractButton(Container cont, String text, StringComparator stringComparator) {
        return waitAbstractButton(cont, text, stringComparator, 0);
    }

    private static class AbstractButtonOperatorSelectedPredicate<T extends AbstractButtonOperator>
            implements Predicate<T> {
        private final boolean selected;

        public AbstractButtonOperatorSelectedPredicate(boolean selected) {
            this.selected = selected;
        }

        @Override
        public boolean test(T absButtonOp) {
            return absButtonOp.isSelected() == selected;
        }
    }
}
