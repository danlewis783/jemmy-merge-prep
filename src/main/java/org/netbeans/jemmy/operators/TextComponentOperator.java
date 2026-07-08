/*
 * Copyright (c) 1997, 2020, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.TextComponent;
import java.awt.event.TextListener;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.TextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;

public class TextComponentOperator extends ComponentOperator {
    private final TextDriver driver;

    public static TextComponentOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    TextComponentOperator(TextComponent b) {
        super(b);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getTextDriver(getClass());
    }

    public static TextComponentOperator of(TextComponent b) {
        return new TextComponentOperator(b);
    }

    public static TextComponentOperator waitFor(ContainerOperator cont, int index) {
        return new TextComponentOperator(
                (TextComponent) waitComponent(cont, PredicatesJ.of(TextComponent.class), index));
    }

    public static TextComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    public static TextComponentOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    public static TextComponentOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new TextComponentOperator(
                (TextComponent) cont.waitSubComponent(PredicatesJ.of(TextComponent.class, chooser), index));
    }

    public static TextComponentOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new TextComponentOperator(
                (TextComponent) waitComponent(cont, new TextComponentByTextPredicate(text, stringComparator), index));
    }

    public void changeCaretPosition(int position) {
        makeComponentVisible();
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.changeCaretPosition(TextComponentOperator.this, position);

                    return null;
                },
                null,
                TimeoutKey.TextComponentOperator_ChangeCaretPositionTimeout);
    }

    public void selectText(int startPosition, int finalPosition) {
        makeComponentVisible();
        produceTimeRestricted(
                (Function<Void, Void>) obj -> {
                    driver.selectText(TextComponentOperator.this, startPosition, finalPosition);

                    return null;
                },
                null,
                TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public int getPositionByText(String text, int index) {
        String allText = getText();
        int position = 0;
        int ind = 0;
        while ((position = allText.indexOf(text, position)) >= 0) {
            if (ind == index) {
                return position;
            } else {
                ind++;
            }

            position = position + text.length();
        }

        return -1;
    }

    public int getPositionByText(String text) {
        return getPositionByText(text, 0);
    }

    public void clearText() {
        makeComponentVisible();
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.clearText(TextComponentOperator.this);

                    return null;
                },
                null,
                TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void typeText(String text, int caretPosition) {
        makeComponentVisible();
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.typeText(TextComponentOperator.this, text, caretPosition);

                    return null;
                },
                null,
                TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void typeText(String text) {
        typeText(text, getCaretPosition());
    }

    public void enterText(String text) {
        makeComponentVisible();
        produceTimeRestricted(
                (Function<Void, Void>) v -> {
                    driver.enterText(TextComponentOperator.this, text);

                    return null;
                },
                null,
                TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void addTextListener(TextListener textListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).addTextListener(textListener);

            return null;
        }));
    }

    public int getCaretPosition() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getCaretPosition()));
    }

    public String getSelectedText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectedText()));
    }

    public int getSelectionEnd() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectionEnd()));
    }

    public int getSelectionStart() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectionStart()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getText()));
    }

    public boolean isEditable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).isEditable()));
    }

    public void removeTextListener(TextListener textListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).removeTextListener(textListener);

            return null;
        }));
    }

    public void select(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).select(i, i1);

            return null;
        }));
    }

    public void selectAll() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).selectAll();

            return null;
        }));
    }

    public void setCaretPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setCaretPosition(i);

            return null;
        }));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setEditable(b);

            return null;
        }));
    }

    public void setSelectionEnd(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setSelectionEnd(i);

            return null;
        }));
    }

    public void setSelectionStart(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setSelectionStart(i);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setText(string);

            return null;
        }));
    }

    protected TextDriver getTextDriver() {
        return driver;
    }

    public static @Nullable TextComponent findTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (TextComponent) findComponent(cont, PredicatesJ.of(TextComponent.class, chooser), index);
    }

    public static @Nullable TextComponent findTextComponent(Container cont, Predicate<Component> chooser) {
        return findTextComponent(cont, chooser, 0);
    }

    public static @Nullable TextComponent findTextComponent(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findTextComponent(cont, new TextComponentByTextPredicate(text, stringComparator), index);
    }

    public static @Nullable TextComponent findTextComponent(
            Container cont, String text, StringComparator stringComparator) {
        return findTextComponent(cont, text, stringComparator, 0);
    }

    public static TextComponent waitTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (TextComponent) waitComponent(cont, PredicatesJ.of(TextComponent.class, chooser), index);
    }

    public static TextComponent waitTextComponent(Container cont, Predicate<Component> chooser) {
        return waitTextComponent(cont, chooser, 0);
    }

    public static TextComponent waitTextComponent(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitTextComponent(cont, new TextComponentByTextPredicate(text, stringComparator), index);
    }

    public static TextComponent waitTextComponent(Container cont, String text, StringComparator stringComparator) {
        return waitTextComponent(cont, text, stringComparator, 0);
    }
}
