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
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuBar;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FrameDriver;
import org.netbeans.jemmy.functions.FrameFunction;
import org.netbeans.jemmy.predicates.FrameOperatorShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.FrameShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public class FrameOperator extends WindowOperator {
    private final FrameDriver driver;

    public static FrameOperator waitFor() {
        return waitFor(0);
    }

    FrameOperator(Frame w) {
        super(w);
        driver = DriverManager.newInstance(JemmyContext.getInstance()).getFrameDriver(getClass());
    }

    public static FrameOperator of(Frame w) {
        return new FrameOperator(w);
    }

    public static FrameOperator waitFor(int index) {
        return new FrameOperator(waitFrame(PredicatesJ.of(Frame.class), index));
    }

    public static FrameOperator waitFor(Predicate<Component> chooser) {
        return waitFor(chooser, 0);
    }

    public static FrameOperator waitFor(String title) {
        return waitFor(title, 0);
    }

    public static FrameOperator waitFor(Predicate<Component> chooser, int index) {
        return new FrameOperator(waitFrame(PredicatesJ.of(Frame.class, chooser), index));
    }

    public static FrameOperator waitFor(String title, int index) {
        return new FrameOperator(waitFrame(new FrameShowingByTitlePredicate(title, StringComparators.strict()), index));
    }

    public void waitTitle(String title, StringComparator stringComparator) {
        waitState(new FrameOperatorShowingByTitlePredicate<>(title, stringComparator));
    }

    public void iconify() {
        driver.iconify(this);

        if (getVerification()) {
            waitState(Frame.ICONIFIED);
        }
    }

    public void deiconify() {
        driver.deiconify(this);

        if (getVerification()) {
            waitState(Frame.NORMAL);
        }
    }

    public void maximize() {
        driver.maximize(this);

        if (getVerification()) {
            waitExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    public void demaximize() {
        driver.demaximize(this);

        if (getVerification()) {
            waitExtendedState(Frame.NORMAL);
        }
    }

    public void waitState(int state) {
        waitState(new FrameOperatorState(state));
    }

    public void waitExtendedState(int state) {
        waitState(new FrameOperatorExtendedState(state));
    }

    public Image getIconImage() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getIconImage()));
    }

    public MenuBar getMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getMenuBar()));
    }

    public int getExtendedState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getExtendedState()));
    }

    public int getState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getState()));
    }

    public String getTitle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).getTitle()));
    }

    public boolean isResizable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Frame) getSource()).isResizable()));
    }

    public void setIconImage(Image image) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setIconImage(image);

            return null;
        }));
    }

    public void setMenuBar(MenuBar menuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setMenuBar(menuBar);

            return null;
        }));
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setResizable(b);

            return null;
        }));
    }

    public void setExtendedState(int state) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setExtendedState(state);

            return null;
        }));
    }

    public void setState(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setState(i);

            return null;
        }));
    }

    public void setTitle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Frame) getSource()).setTitle(string);

            return null;
        }));
    }

    protected static Frame waitFrame(Predicate<Component> predicate, int index) {
        return FunctionRepeater.on(
                        new FrameFunction(
                                index, null, PredicatesJ.of(Frame.class, PredicatesJ.of(Frame.class, predicate))),
                        TimeoutKey.FrameWaiter_WaitFrameTimeout,
                        TimeoutKey.Waiter_TimeDelta)
                .runUntilNotNull(null);
    }

    private static class FrameOperatorState implements Predicate<FrameOperator> {
        private final int state;

        public FrameOperatorState(int state) {
            this.state = state;
        }

        @Override
        public boolean test(FrameOperator frameOp) {
            return frameOp.getState() == state;
        }
    }

    private static class FrameOperatorExtendedState implements Predicate<FrameOperator> {
        private final int state;

        public FrameOperatorExtendedState(int state) {
            this.state = state;
        }

        @Override
        public boolean test(FrameOperator frameOp) {
            return frameOp.getExtendedState() == state;
        }
    }
}
