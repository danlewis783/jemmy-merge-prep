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

package org.netbeans.jemmy.drivers.text;

import java.awt.event.KeyEvent;
import java.util.List;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.JTextComponentOperator;

public abstract class TextAPIDriver extends LightSupportiveDriver implements TextDriver {
    public TextAPIDriver(List<? extends Class<? extends ComponentOperator>> supported) {
        super(supported);
    }

    @Override
    public void changeCaretPosition(ComponentOperator oper, int position) {
        checkSupported(oper);
        ((JTextComponentOperator) oper).setCaretPosition(position);
    }

    @Override
    public void selectText(ComponentOperator oper, int startPosition, int finalPosition) {
        checkSupported(oper);
        int start = Math.min(startPosition, finalPosition);
        int end = Math.max(startPosition, finalPosition);
        JTextComponentOperator toper = (JTextComponentOperator) oper;
        // one EDT hop: set both bounds together so no observer sees a mismatched selection
        QueueTool.getInstance().runOnQueue(() -> {
            toper.setSelectionStart(start);
            toper.setSelectionEnd(end);
        });
    }

    @Override
    public void clearText(ComponentOperator oper) {
        ((JTextComponentOperator) oper).setText("");
    }

    @Override
    public void typeText(ComponentOperator oper, String text, int caretPosition) {
        checkSupported(oper);
        // one EDT snapshot: the text and its selection bounds must describe the same moment,
        // since reading them across separate hops could straddle a selection change
        TextSnapshot snapshot = QueueTool.getInstance()
                .callOnQueue(() -> new TextSnapshot(getText(oper), getSelectionStart(oper), getSelectionEnd(oper)));
        String curtext = snapshot.text;
        int realPos = caretPosition;
        if ((snapshot.selectionStart == realPos) || (snapshot.selectionEnd == realPos)) {
            if (snapshot.selectionEnd == realPos) {
                realPos = realPos - (snapshot.selectionEnd - snapshot.selectionStart);
            }

            curtext = curtext.substring(0, snapshot.selectionStart) + curtext.substring(snapshot.selectionEnd);
        }

        changeText(oper, curtext.substring(0, realPos) + text + curtext.substring(realPos));
    }

    @Override
    public void changeText(ComponentOperator oper, String text) {
        checkSupported(oper);
        ((JTextComponentOperator) oper).setText(text);
    }

    @Override
    public void enterText(ComponentOperator oper, String text) {
        changeText(oper, text);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getKeyDriver(oper)
                .pushKey(oper, KeyEvent.VK_ENTER, 0, TimeoutKey.TextApiDriver_EnterText);
    }

    public abstract String getText(ComponentOperator oper);

    public abstract int getCaretPosition(ComponentOperator oper);

    public abstract int getSelectionStart(ComponentOperator oper);

    public abstract int getSelectionEnd(ComponentOperator oper);

    /** Holder for the text and selection bounds {@link #typeText} reads from one EDT snapshot. */
    private static final class TextSnapshot {
        private final String text;
        private final int selectionStart;
        private final int selectionEnd;

        TextSnapshot(String text, int selectionStart, int selectionEnd) {
            this.text = text;
            this.selectionStart = selectionStart;
            this.selectionEnd = selectionEnd;
        }
    }
}
