/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Window;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.functions.WindowFunction;
import org.netbeans.jemmy.predicates.ComponentOperatorIsVisiblePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowOperator extends ContainerOperator {
    private static final Logger logger = LoggerFactory.getLogger(WindowOperator.class);
    private final WindowDriver driver;

    public WindowOperator() {
        this(0);
    }

    public WindowOperator(int index) {
        this(waitWindow(PredicatesJ.alwaysTrue(), index));
    }

    public WindowOperator(Window w) {
        super(w);
        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        driver = driverManager.getWindowDriver(getClass());
    }

    public WindowOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public WindowOperator(WindowOperator owner, int index) {
        this(waitWindow(owner, PredicatesJ.alwaysTrue(), index));
    }

    public WindowOperator(WindowOperator owner, Predicate<Component> predicate) {
        this(owner, predicate, 0);
    }

    public WindowOperator(WindowOperator owner, Predicate<Component> predicate, int index) {
        this(owner.waitSubWindow(predicate, index));
    }

    public void activate() {
        driver.activate(this);
    }

    public void requestClose() {
        driver.requestClose(this);
    }

    public void requestCloseAndThenHide() {
        driver.requestCloseAndThenHide(this);

        if (getVerification()) {
            waitClosed();
        }
    }

    @Deprecated
    public void close() {
        driver.close(this);

        if (getVerification()) {
            waitClosed();
        }
    }

    public void move(int x, int y) {
        driver.move(this, x, y);
    }

    public void resize(int width, int height) {
        driver.resize(this, width, height);
    }

    public @Nullable Window findSubWindow(Predicate<Component> predicate, int index) {
        return findWindow((Window) getSource(), predicate, index);
    }

    public @Nullable Window findSubWindow(Predicate<Component> predicate) {
        return findSubWindow(predicate, 0);
    }

    public Window waitSubWindow(Predicate<Component> predicate, int index) {
        try {
            return FunctionRepeater.on(
                            new WindowFunction<>(index, (Window) getSource(), predicate),
                            TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting for \"" + predicate.toString() + "\" window has been interrupted", e);
        }
    }

    public Window waitSubWindow(Predicate<Component> chooser) {
        return waitSubWindow(chooser, 0);
    }

    public static void waitWindowCount(Predicate<Component> chooser, int count) {
        waitWindowCount(null, chooser, count);
    }

    public static void waitWindowCount(@Nullable Window owner, Predicate<Component> chooser, int count) {
        try {
            FunctionRepeater.on(
                            (java.util.function.Function<Void, Boolean>)
                                    unused -> (countWindows(owner, chooser) == count) ? true : null,
                            TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException(
                    "Waiting for " + count + " windows matching \"" + chooser + "\" has been interrupted", e);
        }
    }

    public static int countWindows(Predicate<Component> chooser) {
        return countWindows(null, chooser);
    }

    public static int countWindows(@Nullable Window owner, Predicate<Component> chooser) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            Window[] windows = (owner == null) ? Window.getWindows() : owner.getOwnedWindows();
            int matches = 0;
            for (Window window : windows) {
                if (chooser.test(window)) {
                    matches++;
                }
            }

            return matches;
        }));
    }

    public void waitClosed() {
        waitState(new ComponentOperatorIsVisiblePredicate<WindowOperator>(false));
    }

    public void addWindowListener(WindowListener windowListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).addWindowListener(windowListener);

            return null;
        }));
    }

    public void applyResourceBundle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).applyResourceBundle(string);

            return null;
        }));
    }

    public void applyResourceBundle(ResourceBundle resourceBundle) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).applyResourceBundle(resourceBundle);

            return null;
        }));
    }

    public void dispose() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).dispose();

            return null;
        }));
    }

    public Component getFocusOwner() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Window) getSource()).getFocusOwner()));
    }

    public Window[] getOwnedWindows() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Window) getSource()).getOwnedWindows()));
    }

    public Window getOwner() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Window) getSource()).getOwner()));
    }

    public String getWarningString() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Window) getSource()).getWarningString()));
    }

    public void pack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).pack();

            return null;
        }));
    }

    public void removeWindowListener(WindowListener windowListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).removeWindowListener(windowListener);

            return null;
        }));
    }

    public void toBack() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).toBack();

            return null;
        }));
    }

    public void toFront() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Window) getSource()).toFront();

            return null;
        }));
    }

    public boolean isFocused() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            try {
                return (Boolean) new ClassReference<>(getSource()).invokeMethod("isFocused", null, null);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                logger.warn("", e);

                return false;
            }
        }));
    }

    public boolean isActive() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            try {
                return (Boolean) new ClassReference<>(getSource()).invokeMethod("isActive", null, null);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                logger.warn("", e);

                return false;
            }
        }));
    }

    public static @Nullable Window findWindow(Predicate<Component> chooser, int index) {
        return WindowFunction.getWindow(null, chooser, index);
    }

    public static @Nullable Window findWindow(Predicate<Component> chooser) {
        return findWindow(chooser, 0);
    }

    public static @Nullable Window findWindow(Window owner, Predicate<Component> chooser, int index) {
        return WindowFunction.getWindow(owner, chooser, index);
    }

    public static @Nullable Window findWindow(Window owner, Predicate<Component> chooser) {
        return findWindow(owner, chooser, 0);
    }

    public static Window waitWindow(Predicate<Component> chooser) {
        return waitWindow(chooser, 0);
    }

    public static Window waitWindow(Window owner, Predicate<Component> chooser) {
        return waitWindow(owner, chooser, 0);
    }

    protected static Window waitWindow(Predicate<Component> chooser, int index) {
        try {
            return FunctionRepeater.on(
                            new WindowFunction<>(index, null, chooser), TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new JemmyException("Waiting for \"" + chooser.toString() + "\" window has been interrupted", e);
        }
    }

    protected static Window waitWindow(WindowOperator owner, Predicate<Component> chooser, int index) {
        return waitWindow((Window) owner.getSource(), chooser, index);
    }

    protected static Window waitWindow(Window owner, Predicate<Component> chooser, int index) {
        try {
            return FunctionRepeater.on(
                            new WindowFunction<>(index, owner, chooser), TimeoutKey.WindowWaiter_WaitWindowTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
