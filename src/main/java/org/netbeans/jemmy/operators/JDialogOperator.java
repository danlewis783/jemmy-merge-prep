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
import java.awt.Dialog;
import java.awt.Window;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.accessibility.AccessibleContext;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.DialogFunction;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.TopVisibleModalDialogPredicate;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDialogOperator extends DialogOperator {
    private static final Logger logger = LoggerFactory.getLogger(JDialogOperator.class);

    public JDialogOperator() {
        this(0);
    }

    public JDialogOperator(int index) {
        this(waitJDialog(PredicatesJ.of(JDialog.class), index));
    }

    public JDialogOperator(JDialog w) {
        super(w);
    }

    public JDialogOperator(Predicate<Component> predicate) {
        this(predicate, 0);
    }

    public JDialogOperator(String title) {
        this(title, 0);
    }

    public JDialogOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public JDialogOperator(Predicate<Component> predicate, int index) {
        this(waitJDialog(PredicatesJ.of(JDialog.class, predicate), index));
    }

    public JDialogOperator(String title, int index) {
        this(
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, StringComparators.strict())),
                index);
    }

    public JDialogOperator(WindowOperator owner, int index) {
        this(waitJDialog(owner, PredicatesJ.of(JDialog.class), index));
    }

    public JDialogOperator(WindowOperator owner, Predicate<Component> predicate) {
        this(owner, predicate, 0);
    }

    public JDialogOperator(WindowOperator owner, String title, StringComparator stringComparator) {
        this(owner, title, stringComparator, 0);
    }

    public JDialogOperator(WindowOperator owner, Predicate<Component> predicate, int index) {
        this((JDialog) owner.waitSubWindow(PredicatesJ.of(JDialog.class, predicate), index));
    }

    public JDialogOperator(WindowOperator owner, String title, StringComparator stringComparator, int index) {
        this(waitJDialog(
                owner,
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)),
                index));
    }

    public AccessibleContext getAccessibleContext() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> getSource().getAccessibleContext()));
    }

    public Container getContentPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getContentPane()));
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getDefaultCloseOperation()));
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getGlassPane()));
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getJMenuBar()));
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getLayeredPane()));
    }

    public JRootPane getRootPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getRootPane()));
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setContentPane(container);

            return null;
        }));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setDefaultCloseOperation(i);

            return null;
        }));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setGlassPane(component);

            return null;
        }));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setJMenuBar(jMenuBar);

            return null;
        }));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setLayeredPane(jLayeredPane);

            return null;
        }));
    }

    public void setLocationRelativeTo(@Nullable Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setLocationRelativeTo(component);

            return null;
        }));
    }

    public static @Nullable JDialog findJDialog(Predicate<Component> predicate, int index) {
        return (JDialog) DialogFunction.getDialog(PredicatesJ.of(JDialog.class, predicate), index);
    }

    public static @Nullable JDialog findJDialog(Predicate<Component> predicate) {
        return findJDialog(predicate, 0);
    }

    public static @Nullable JDialog findJDialog(String title, StringComparator stringComparator, int index) {
        return (JDialog) DialogFunction.getDialog(
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)), index);
    }

    public static @Nullable JDialog findJDialog(String title, StringComparator stringComparator) {
        return findJDialog(title, stringComparator, 0);
    }

    public static @Nullable JDialog findJDialog(Window owner, Predicate<Component> predicate, int index) {
        return (JDialog) DialogFunction.getDialog(owner, PredicatesJ.of(JDialog.class, predicate), index);
    }

    public static @Nullable JDialog findJDialog(Window owner, Predicate<Component> predicate) {
        return findJDialog(owner, predicate, 0);
    }

    public static @Nullable JDialog findJDialog(
            Window owner, String title, StringComparator stringComparator, int index) {
        return (JDialog) DialogFunction.getDialog(
                owner,
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)),
                index);
    }

    public static @Nullable JDialog findJDialog(Window owner, String title, StringComparator stringComparator) {
        return findJDialog(owner, title, stringComparator, 0);
    }

    public static JDialog waitJDialog(Predicate<Component> predicate) {
        return waitJDialog(predicate, 0);
    }

    public static JDialog waitJDialog(String title, StringComparator stringComparator, int index) {
        return waitJDialog(
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)), index);
    }

    public static JDialog waitJDialog(String title, StringComparator stringComparator) {
        return waitJDialog(title, stringComparator, 0);
    }

    public static JDialog waitJDialog(Window owner, Predicate<Component> predicate) {
        return waitJDialog(owner, predicate, 0);
    }

    public static JDialog waitJDialog(Window owner, String title, StringComparator stringComparator, int index) {
        return waitJDialog(
                owner,
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)),
                index);
    }

    public static JDialog waitJDialog(Window owner, String title, StringComparator stringComparator) {
        return waitJDialog(owner, title, stringComparator, 0);
    }

    public static @Nullable Dialog getTopModalDialog() {
        return DialogFunction.getDialog(new TopVisibleModalDialogPredicate());
    }

    protected static JDialog waitJDialog(Predicate<Component> predicate, int index) {
        return waitJDialog((Window) null, predicate, index);
    }

    protected static JDialog waitJDialog(WindowOperator owner, Predicate<Component> predicate, int index) {
        return waitJDialog((Window) owner.getSource(), predicate, index);
    }

    protected static JDialog waitJDialog(@Nullable Window owner, Predicate<Component> predicate, int index) {
        return (JDialog) FunctionRepeater.on(
                        new DialogFunction(index, owner, PredicatesJ.of(JDialog.class, predicate)),
                        TimeoutKey.DialogWaiter_WaitDialogTimeout)
                .runUntilNotNull(null);
    }
}
