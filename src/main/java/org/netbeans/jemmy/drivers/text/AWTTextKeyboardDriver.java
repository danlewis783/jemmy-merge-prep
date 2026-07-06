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
import java.util.Collections;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.TextAreaOperator;
import org.netbeans.jemmy.operators.TextComponentOperator;

public final class AWTTextKeyboardDriver extends TextKeyboardDriver {
    public AWTTextKeyboardDriver() {
        super(Collections.singletonList(TextComponentOperator.class));
    }

    @Override
    public String getText(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getText();
    }

    @Override
    public int getCaretPosition(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getCaretPosition();
    }

    @Override
    public int getSelectionStart(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionStart();
    }

    @Override
    public int getSelectionEnd(ComponentOperator oper) {
        return ((TextComponentOperator) oper).getSelectionEnd();
    }

    @Override
    public NavigationKey[] getKeys(ComponentOperator oper) {
        boolean multiString = oper instanceof TextAreaOperator;
        NavigationKey[] result = new NavigationKey[multiString ? 4 : 2];
        result[0] = new UpKey(KeyEvent.VK_LEFT, 0);
        result[1] = new DownKey(KeyEvent.VK_RIGHT, 0);
        ((UpKey) result[0]).setDownKey((DownKey) result[1]);
        ((DownKey) result[1]).setUpKey((UpKey) result[0]);

        if (multiString) {
            result[2] = new UpKey(KeyEvent.VK_UP, 0);
            result[3] = new DownKey(KeyEvent.VK_DOWN, 0);
            ((UpKey) result[2]).setDownKey((DownKey) result[3]);
            ((DownKey) result[3]).setUpKey((UpKey) result[2]);
        }

        return result;
    }

    @Override
    public TimeoutKey getBetweenTimeout(ComponentOperator oper) {
        return TimeoutKey.TextComponentOperator_BetweenKeysTimeout;
    }
}
