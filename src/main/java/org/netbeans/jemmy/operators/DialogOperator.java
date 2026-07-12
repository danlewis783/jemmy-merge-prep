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
import java.awt.Dialog;
import java.awt.Window;
import java.util.function.Predicate;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.DialogFunction;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.DialogOperatorShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.util.StringComparator;

public class DialogOperator extends WindowOperator {

    public static DialogOperator waitFor() {
        return waitFor(0);
    }

    /**
     * @deprecated Use {@link #waitFor()} instead.
     */
    @Deprecated
    public DialogOperator() {
        this(0);
    }

    /**
     * @deprecated Use {@link #of(Dialog)} instead.
     */
    @Deprecated
    public DialogOperator(Dialog w) {
        super(w);
    }

    public static DialogOperator of(Dialog w) {
        return new DialogOperator(w);
    }

    public static DialogOperator waitFor(int index) {
        return new DialogOperator(waitDialog(ComponentPredicates.of(Dialog.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(int)} instead.
     */
    @Deprecated
    public DialogOperator(int index) {
        this(waitDialog(ComponentPredicates.of(Dialog.class), index));
    }

    public static DialogOperator waitFor(Predicate<Component> chooser) {
        return waitFor(chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(Predicate)} instead.
     */
    @Deprecated
    public DialogOperator(Predicate<Component> chooser) {
        this(chooser, 0);
    }

    public static DialogOperator waitFor(String title) {
        return waitFor(title, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(String)} instead.
     */
    @Deprecated
    public DialogOperator(String title) {
        this(title, 0);
    }

    public static DialogOperator waitFor(WindowOperator owner) {
        return waitFor(owner, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public static DialogOperator waitFor(Predicate<Component> chooser, int index) {
        return new DialogOperator(waitDialog(ComponentPredicates.of(Dialog.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(Predicate, int)} instead.
     */
    @Deprecated
    public DialogOperator(Predicate<Component> chooser, int index) {
        this(waitDialog(ComponentPredicates.of(Dialog.class, chooser), index));
    }

    public static DialogOperator waitFor(String title, int index) {
        return waitFor(new DialogShowingByTitlePredicate(title), index);
    }

    /**
     * @deprecated Use {@link #waitFor(String, int)} instead.
     */
    @Deprecated
    public DialogOperator(String title, int index) {
        this(new DialogShowingByTitlePredicate(title), index);
    }

    public static DialogOperator waitFor(WindowOperator owner, int index) {
        return new DialogOperator(waitDialog(owner, ComponentPredicates.of(Dialog.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, int)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner, int index) {
        this(waitDialog(owner, ComponentPredicates.of(Dialog.class), index));
    }

    public static DialogOperator waitFor(WindowOperator owner, Predicate<Component> chooser) {
        return waitFor(owner, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, Predicate)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner, Predicate<Component> chooser) {
        this(owner, chooser, 0);
    }

    public static DialogOperator waitFor(WindowOperator owner, String title, StringComparator stringComparator) {
        return waitFor(owner, title, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner, String title, StringComparator stringComparator) {
        this(owner, title, stringComparator, 0);
    }

    public static DialogOperator waitFor(WindowOperator owner, Predicate<Component> chooser, int index) {
        return new DialogOperator((Dialog) owner.waitSubWindow(ComponentPredicates.of(Dialog.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, Predicate, int)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner, Predicate<Component> chooser, int index) {
        this((Dialog) owner.waitSubWindow(ComponentPredicates.of(Dialog.class, chooser), index));
    }

    public static DialogOperator waitFor(
            WindowOperator owner, String title, StringComparator stringComparator, int index) {
        return new DialogOperator(waitDialog(owner, new DialogShowingByTitlePredicate(title, stringComparator), index));
    }

    /**
     * @deprecated Use {@link #waitFor(WindowOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public DialogOperator(WindowOperator owner, String title, StringComparator stringComparator, int index) {
        this(waitDialog(owner, new DialogShowingByTitlePredicate(title, stringComparator), index));
    }

    public void waitTitle(String title, StringComparator stringComparator) {
        waitState(new DialogOperatorShowingByTitlePredicate(title, stringComparator));
    }

    public String getTitle() {
        return QueueTool.getInstance().callOnQueue(() -> ((Dialog) getSource()).getTitle());
    }

    public boolean isModal() {
        return QueueTool.getInstance().callOnQueue(() -> ((Dialog) getSource()).isModal());
    }

    public boolean isResizable() {
        return QueueTool.getInstance().callOnQueue(() -> ((Dialog) getSource()).isResizable());
    }

    public void setModal(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((Dialog) getSource()).setModal(b));
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().runOnQueue(() -> ((Dialog) getSource()).setResizable(b));
    }

    public void setTitle(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((Dialog) getSource()).setTitle(string));
    }

    protected static Dialog waitDialog(Predicate<Component> chooser, int index) {
        return FunctionRepeater.on(
                        new DialogFunction(index, null, ComponentPredicates.of(Dialog.class, chooser)),
                        TimeoutKey.DialogWaiter_WaitDialogTimeout)
                .runUntilNotNull(null);
    }

    protected static Dialog waitDialog(WindowOperator owner, Predicate<Component> chooser, int index) {
        return waitDialog((Window) owner.getSource(), chooser, index);
    }

    protected static Dialog waitDialog(Window owner, Predicate<Component> chooser, int index) {
        return FunctionRepeater.on(
                        new DialogFunction(index, owner, ComponentPredicates.of(Dialog.class, chooser)),
                        TimeoutKey.DialogWaiter_WaitDialogTimeout)
                .runUntilNotNull(null);
    }
}
