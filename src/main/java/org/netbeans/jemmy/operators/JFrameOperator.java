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
import java.awt.Frame;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.accessibility.AccessibleContext;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.FrameFunction;
import org.netbeans.jemmy.predicates.FrameShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JFrameOperator extends FrameOperator {
    private static final Logger logger = LoggerFactory.getLogger(JFrameOperator.class);

    public JFrameOperator() {
        this(0);
    }

    public JFrameOperator(int index) {
        this((JFrame) waitFrame(PredicatesJ.of(JFrame.class), index));
    }

    public JFrameOperator(JFrame w) {
        super(w);
    }

    public JFrameOperator(Predicate<Component> chooser) {
        this(chooser, 0);
    }

    public JFrameOperator(String title) {
        this(title, 0);
    }

    public JFrameOperator(Predicate<Component> chooser, int index) {
        this((JFrame) waitFrame(PredicatesJ.of(JFrame.class, chooser), index));
    }

    public JFrameOperator(String title, int index) {
        this(PredicatesJ.of(JFrame.class, new FrameShowingByTitlePredicate(title, StringComparators.strict())), index);
    }

    public AccessibleContext getAccessibleContext() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getAccessibleContext()));
    }

    public Container getContentPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getContentPane()));
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getDefaultCloseOperation()));
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getGlassPane()));
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getJMenuBar()));
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getLayeredPane()));
    }

    public JRootPane getRootPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JFrame) getSource()).getRootPane()));
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFrame) getSource()).setContentPane(container);

            return null;
        }));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFrame) getSource()).setDefaultCloseOperation(i);

            return null;
        }));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFrame) getSource()).setGlassPane(component);

            return null;
        }));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFrame) getSource()).setJMenuBar(jMenuBar);

            return null;
        }));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JFrame) getSource()).setLayeredPane(jLayeredPane);

            return null;
        }));
    }

    public static JFrame findJFrame(Predicate<Component> chooser, int index) {
        return (JFrame) FrameFunction.getFrame(PredicatesJ.of(JFrame.class, chooser), index);
    }

    public static JFrame findJFrame(Predicate<Component> chooser) {
        return findJFrame(chooser, 0);
    }

    public static JFrame findJFrame(String title, StringComparator stringComparator, int index) {
        return (JFrame) FrameFunction.getFrame(
                PredicatesJ.of(JFrame.class, new FrameShowingByTitlePredicate(title, stringComparator)), index);
    }

    public static JFrame findJFrame(String title, StringComparator stringComparator) {
        return findJFrame(title, stringComparator, 0);
    }

    public static JFrame waitJFrame(Predicate<Component> chooser, int index) {
        return (JFrame) waitFrame(PredicatesJ.of(JFrame.class, chooser), index);
    }

    public static JFrame waitJFrame(Predicate<Component> chooser) {
        return waitJFrame(chooser, 0);
    }

    public static JFrame waitJFrame(String title) {
        return waitJFrame(title, StringComparators.strict());
    }

    public static JFrame waitJFrame(String title, StringComparator stringComparator) {
        return waitJFrame(title, stringComparator, 0);
    }

    public static JFrame waitJFrame(String title, StringComparator stringComparator, int index) {
        try {
            return (JFrame) FunctionRepeater.on(
                            new FrameFunction(
                                    index,
                                    null,
                                    PredicatesJ.of(
                                            Frame.class,
                                            PredicatesJ.of(
                                                    JFrame.class,
                                                    new FrameShowingByTitlePredicate(title, stringComparator)))),
                            TimeoutKey.FrameWaiter_WaitFrameTimeout)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            logger.warn("", e);

            return null;
        }
    }
}
