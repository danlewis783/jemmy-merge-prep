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
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.AbstractButtonOperatorByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class AbstractButtonOperator extends JComponentOperator {
    private final ButtonDriver driver;

    public AbstractButtonOperator(AbstractButton b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(getClass());
    }

    public AbstractButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public AbstractButtonOperator(ContainerOperator cont, int index) {
        this((AbstractButton) waitComponent(cont, PredicatesJ.of(AbstractButton.class), index));
    }

    public AbstractButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public AbstractButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public AbstractButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((AbstractButton) cont.waitSubComponent(PredicatesJ.of(AbstractButton.class, chooser), index));
    }

    public AbstractButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((AbstractButton) waitComponent(
                cont,
                PredicatesJ.of(AbstractButton.class, new AbstractButtonByTextPredicate(text, stringComparator)),
                index));
    }

    public void push() {
        makeComponentVisible();

        waitComponentEnabled();

        driver.push(this);
    }

    public <F, T> void pushNoBlock() {
        produceNoBlocking(
                (Function<F, T>) f -> {
                    push();

                    return null;
                },
                null);
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
        produceNoBlocking(
                (Function<Boolean, Void>) param -> {
                    changeSelection(param);

                    return null;
                },
                selected);
    }

    public void press() {
        makeComponentVisible();

        waitComponentEnabled();

        driver.press(this);
    }

    public void release() {
        waitComponentEnabled();

        driver.release(this);
    }

    public void waitSelected(boolean selected) {
        waitState(new AbstractButtonOperatorSelectedPredicate<>(selected));
    }

    public void waitText(String text, StringComparator stringComparator) {
        waitState(new AbstractButtonOperatorByTextPredicate<>(text, stringComparator));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public void addChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).addChangeListener(changeListener);

            return null;
        }));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).addItemListener(itemListener);

            return null;
        }));
    }

    public void doClick() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).doClick();

            return null;
        }));
    }

    public void doClick(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).doClick(i);

            return null;
        }));
    }

    public String getActionCommand() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getActionCommand()));
    }

    public Icon getDisabledIcon() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getDisabledIcon()));
    }

    public Icon getDisabledSelectedIcon() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getDisabledSelectedIcon()));
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getHorizontalAlignment()));
    }

    public int getHorizontalTextPosition() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getHorizontalTextPosition()));
    }

    public Icon getIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getIcon()));
    }

    public Insets getMargin() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getMargin()));
    }

    public int getMnemonic() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getMnemonic()));
    }

    public ButtonModel getModel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getModel()));
    }

    public Icon getPressedIcon() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getPressedIcon()));
    }

    public Icon getRolloverIcon() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getRolloverIcon()));
    }

    public Icon getRolloverSelectedIcon() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getRolloverSelectedIcon()));
    }

    public Icon getSelectedIcon() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getSelectedIcon()));
    }

    public Object[] getSelectedObjects() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getSelectedObjects()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getText()));
    }

    public ButtonUI getUI() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getUI()));
    }

    public int getVerticalAlignment() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getVerticalAlignment()));
    }

    public int getVerticalTextPosition() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).getVerticalTextPosition()));
    }

    public boolean isBorderPainted() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).isBorderPainted()));
    }

    public boolean isContentAreaFilled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).isContentAreaFilled()));
    }

    public boolean isFocusPainted() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).isFocusPainted()));
    }

    public boolean isRolloverEnabled() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).isRolloverEnabled()));
    }

    public boolean isSelected() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((AbstractButton) getSource()).isSelected()));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void removeChangeListener(ChangeListener changeListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).removeChangeListener(changeListener);

            return null;
        }));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).removeItemListener(itemListener);

            return null;
        }));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setActionCommand(string);

            return null;
        }));
    }

    public void setBorderPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setBorderPainted(b);

            return null;
        }));
    }

    public void setContentAreaFilled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setContentAreaFilled(b);

            return null;
        }));
    }

    public void setDisabledIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setDisabledIcon(icon);

            return null;
        }));
    }

    public void setDisabledSelectedIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setDisabledSelectedIcon(icon);

            return null;
        }));
    }

    public void setFocusPainted(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setFocusPainted(b);

            return null;
        }));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setHorizontalAlignment(i);

            return null;
        }));
    }

    public void setHorizontalTextPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setHorizontalTextPosition(i);

            return null;
        }));
    }

    public void setIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setIcon(icon);

            return null;
        }));
    }

    public void setMargin(Insets insets) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setMargin(insets);

            return null;
        }));
    }

    public void setMnemonic(char c) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setMnemonic(c);

            return null;
        }));
    }

    public void setMnemonic(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setMnemonic(i);

            return null;
        }));
    }

    public void setModel(ButtonModel buttonModel) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setModel(buttonModel);

            return null;
        }));
    }

    public void setPressedIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setPressedIcon(icon);

            return null;
        }));
    }

    public void setRolloverEnabled(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setRolloverEnabled(b);

            return null;
        }));
    }

    public void setRolloverIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setRolloverIcon(icon);

            return null;
        }));
    }

    public void setRolloverSelectedIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setRolloverSelectedIcon(icon);

            return null;
        }));
    }

    public void setSelected(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setSelected(b);

            return null;
        }));
    }

    public void setSelectedIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setSelectedIcon(icon);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setText(string);

            return null;
        }));
    }

    public void setUI(ButtonUI buttonUI) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setUI(buttonUI);

            return null;
        }));
    }

    public void setVerticalAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setVerticalAlignment(i);

            return null;
        }));
    }

    public void setVerticalTextPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((AbstractButton) getSource()).setVerticalTextPosition(i);

            return null;
        }));
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
