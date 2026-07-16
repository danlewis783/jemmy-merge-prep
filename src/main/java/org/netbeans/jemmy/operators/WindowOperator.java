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
import java.util.ResourceBundle;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.BooleanSupplierRepeater;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.WindowDriver;
import org.netbeans.jemmy.functions.WindowFunction;
import org.netbeans.jemmy.predicates.ComponentOperatorIsVisiblePredicate;
import org.netbeans.jemmy.predicates.ComponentPredicates;

public class WindowOperator extends ContainerOperator {
    public static WindowOperator waitFor() {
        return waitFor(0);
    }

    /**
     * @deprecated Use {@link #waitFor()} instead.
     */
    @Deprecated
    public WindowOperator() {
        this(0);
    }

    public static WindowOperator waitFor(int index) {
        return new WindowOperator(waitWindow(ComponentPredicates.alwaysTrue(), index));
    }

    /**
     * @deprecated Use {@link #waitFor(int)} instead.
     */
    @Deprecated
    public WindowOperator(int index) {
        this(waitWindow(ComponentPredicates.alwaysTrue(), index));
    }

    /**
     * @deprecated Use {@link #of(Window)} instead.
     */
    @Deprecated
    public WindowOperator(Window w) {
        super(w);
    }

    private WindowDriver driver() {
        return DriverManager.newInstance(JemmyContext.getInstance()).getWindowDriver(getClass());
    }

    public static WindowOperator of(Window w) {
        return new WindowOperator(w);
    }

    public static WindowOperator waitFor(WindowOperator owner) {
        return waitFor(owner, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator)} instead.
     */
    @Deprecated
    public WindowOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public static WindowOperator waitFor(WindowOperator owner, int index) {
        return new WindowOperator(waitWindow(owner, ComponentPredicates.alwaysTrue(), index));
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, int)} instead.
     */
    @Deprecated
    public WindowOperator(WindowOperator owner, int index) {
        this(waitWindow(owner, ComponentPredicates.alwaysTrue(), index));
    }

    public static WindowOperator waitFor(WindowOperator owner, Predicate<Component> predicate) {
        return waitFor(owner, predicate, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, Predicate)} instead.
     */
    @Deprecated
    public WindowOperator(WindowOperator owner, Predicate<Component> predicate) {
        this(owner, predicate, 0);
    }

    public static WindowOperator waitFor(WindowOperator owner, Predicate<Component> predicate, int index) {
        return new WindowOperator(owner.waitSubWindow(predicate, index));
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, Predicate, int)} instead.
     */
    @Deprecated
    public WindowOperator(WindowOperator owner, Predicate<Component> predicate, int index) {
        this(owner.waitSubWindow(predicate, index));
    }

    public void activate() {
        driver().activate(this);
    }

    public void requestClose() {
        driver().requestClose(this);
    }

    public void requestCloseAndThenHide() {
        driver().requestCloseAndThenHide(this);

        if (getVerification()) {
            waitClosed();
        }
    }

    @Deprecated
    public void close() {
        driver().close(this);

        if (getVerification()) {
            waitClosed();
        }
    }

    public void move(int x, int y) {
        driver().move(this, x, y);
    }

    public void resize(int width, int height) {
        driver().resize(this, width, height);
    }

    public @Nullable Window findSubWindow(Predicate<Component> predicate, int index) {
        return findWindow((Window) getSource(), predicate, index);
    }

    public @Nullable Window findSubWindow(Predicate<Component> predicate) {
        return findSubWindow(predicate, 0);
    }

    public Window waitSubWindow(Predicate<Component> predicate, int index) {
        return FunctionRepeater.on(
                        new WindowFunction<>(index, (Window) getSource(), predicate),
                        TimeoutKey.WindowWaiter_WaitWindowTimeout)
                .runUntilNotNull(null);
    }

    public Window waitSubWindow(Predicate<Component> chooser) {
        return waitSubWindow(chooser, 0);
    }

    public static void waitWindowCount(Predicate<Component> chooser, int count) {
        waitWindowCount(null, chooser, count);
    }

    public static void waitWindowCount(@Nullable Window owner, Predicate<Component> chooser, int count) {
        BooleanSupplierRepeater.waitFor(
                () -> countWindows(owner, chooser) == count, TimeoutKey.WindowWaiter_WaitWindowTimeout);
    }

    public static int countWindows(Predicate<Component> chooser) {
        return countWindows(null, chooser);
    }

    public static int countWindows(@Nullable Window owner, Predicate<Component> chooser) {
        return QueueTool.getInstance().callOnQueue(() -> {
            Window[] windows = (owner == null) ? Window.getWindows() : owner.getOwnedWindows();
            int matches = 0;
            for (Window window : windows) {
                if (chooser.test(window)) {
                    matches++;
                }
            }

            return matches;
        });
    }

    public void waitClosed() {
        waitState(new ComponentOperatorIsVisiblePredicate<WindowOperator>(false));
    }

    public void addWindowListener(WindowListener windowListener) {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().addWindowListener(windowListener));
    }

    public void applyResourceBundle(String string) {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().applyResourceBundle(string));
    }

    public void applyResourceBundle(ResourceBundle resourceBundle) {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().applyResourceBundle(resourceBundle));
    }

    public void dispose() {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().dispose());
    }

    public @Nullable Component getFocusOwner() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().getFocusOwner());
    }

    public Window[] getOwnedWindows() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().getOwnedWindows());
    }

    public @Nullable Window getOwner() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().getOwner());
    }

    public @Nullable String getWarningString() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().getWarningString());
    }

    public void pack() {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().pack());
    }

    public void removeWindowListener(WindowListener windowListener) {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().removeWindowListener(windowListener));
    }

    public void toBack() {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().toBack());
    }

    public void toFront() {
        QueueTool.getInstance().runOnQueue(() -> getSourceAsWindow().toFront());
    }

    public boolean isFocused() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().isFocused());
    }

    public boolean isActive() {
        return QueueTool.getInstance().callOnQueue(() -> getSourceAsWindow().isActive());
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
        return FunctionRepeater.on(
                        new WindowFunction<>(index, null, chooser), TimeoutKey.WindowWaiter_WaitWindowTimeout)
                .runUntilNotNull(null);
    }

    protected static Window waitWindow(WindowOperator owner, Predicate<Component> chooser, int index) {
        return waitWindow((Window) owner.getSource(), chooser, index);
    }

    protected static Window waitWindow(Window owner, Predicate<Component> chooser, int index) {
        return FunctionRepeater.on(
                        new WindowFunction<>(index, owner, chooser), TimeoutKey.WindowWaiter_WaitWindowTimeout)
                .runUntilNotNull(null);
    }

    private Window getSourceAsWindow() {
        return (Window) getSource();
    }
}
