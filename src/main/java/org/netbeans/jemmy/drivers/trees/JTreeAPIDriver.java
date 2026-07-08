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

package org.netbeans.jemmy.drivers.trees;

import java.util.Collections;
import javax.swing.text.JTextComponent;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.drivers.TreeDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
import org.netbeans.jemmy.predicates.PredicatesJ;

public final class JTreeAPIDriver extends LightSupportiveDriver implements TreeDriver {
    public JTreeAPIDriver() {
        super(Collections.singletonList(JTreeOperator.class));
    }

    @Override
    public void selectItem(ComponentOperator oper, int index) {
        selectItems(oper, new int[] {index});
    }

    @Override
    public void selectItems(ComponentOperator oper, int[] indices) {
        checkSupported(oper);
        ((JTreeOperator) oper).clearSelection();
        ((JTreeOperator) oper).addSelectionRows(indices);
    }

    @Override
    public void expandItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        ((JTreeOperator) oper).expandRow(index);
    }

    @Override
    public void collapseItem(ComponentOperator oper, int index) {
        checkSupported(oper);
        ((JTreeOperator) oper).collapseRow(index);
    }

    @Override
    public void editItem(ComponentOperator oper, int index, Object newValue, TimeoutKey waitEditorTime) {
        JTextComponentOperator textoper = startEditingAndReturnEditor(oper, index, waitEditorTime);
        TextDriver text =
                DriverManager.newInstance(JemmyContext.getInstance()).getTextDriver(JTextComponentOperator.class);
        text.clearText(textoper);
        text.typeText(textoper, newValue.toString(), 0);
        ((JTreeOperator) oper).stopEditing();
    }

    @Override
    public void startEditing(ComponentOperator oper, int index, TimeoutKey waitEditorTime) {
        startEditingAndReturnEditor(oper, index, waitEditorTime);
    }

    private JTextComponentOperator startEditingAndReturnEditor(
            ComponentOperator oper, int index, TimeoutKey waitEditorTime) {
        checkSupported(oper);
        JTreeOperator jTreeOperator = (JTreeOperator) oper;
        jTreeOperator.startEditingAtPath(jTreeOperator.getPathForRow(index));
        JTextComponent jTextComponent =
                (JTextComponent) jTreeOperator.waitSubComponent(PredicatesJ.of(JTextComponent.class), waitEditorTime);
        return new JTextComponentOperator(jTextComponent);
    }
}
