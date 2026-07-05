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
import java.awt.Label;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.LabelByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

public class LabelOperator extends ComponentOperator {
    public LabelOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public LabelOperator(Label b) {
        super(b);
    }

    public LabelOperator(ContainerOperator cont, int index) {
        this((Label) waitComponent(cont, PredicatesJ.of(Label.class), index));
    }

    public LabelOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public LabelOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Label) cont.waitSubComponent(PredicatesJ.of(Label.class, chooser), index));
    }

    public LabelOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this((Label) waitComponent(cont, new LabelByLabelPredicate(text, stringComparator), 0));
    }

    public LabelOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Label) waitComponent(cont, new LabelByLabelPredicate(text, stringComparator), index));
    }

    public int getAlignment() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Label) getSource()).getAlignment()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Label) getSource()).getText()));
    }

    public void setAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Label) getSource()).setAlignment(i);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Label) getSource()).setText(string);

            return null;
        }));
    }

    public static Label findLabel(Container cont, Predicate<Component> chooser, int index) {
        return (Label) findComponent(cont, PredicatesJ.of(Label.class, chooser), index);
    }

    public static Label findLabel(Container cont, Predicate<Component> chooser) {
        return findLabel(cont, chooser, 0);
    }

    public static Label findLabel(Container cont, String text, StringComparator stringComparator, int index) {
        return findLabel(cont, new LabelByLabelPredicate(text, stringComparator), index);
    }

    public static Label findLabel(Container cont, String text, StringComparator stringComparator) {
        return findLabel(cont, text, stringComparator, 0);
    }

    public static Label waitLabel(Container cont, Predicate<Component> chooser, int index) {
        return (Label) waitComponent(cont, PredicatesJ.of(Label.class, chooser), index);
    }

    public static Label waitLabel(Container cont, Predicate<Component> chooser) {
        return waitLabel(cont, chooser, 0);
    }

    public static Label waitLabel(Container cont, String text, StringComparator stringComparator, int index) {
        return waitLabel(cont, new LabelByLabelPredicate(text, stringComparator), index);
    }

    public static Label waitLabel(Container cont, String text, StringComparator stringComparator) {
        return waitLabel(cont, text, stringComparator, 0);
    }
}
