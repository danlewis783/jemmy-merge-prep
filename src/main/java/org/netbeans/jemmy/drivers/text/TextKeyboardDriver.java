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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import org.netbeans.jemmy.CharBindingMap;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.LightSupportiveDriver;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public abstract class TextKeyboardDriver extends LightSupportiveDriver implements TextDriver {
    public TextKeyboardDriver(List<? extends Class<? extends ComponentOperator>> supported) {
        super(supported);
    }

    @Override
    public void changeCaretPosition(ComponentOperator oper, int position) {
        DriverManager.newInstance(JemmyContext.getInstance())
                .getFocusDriver(oper)
                .giveFocus(oper);
        checkSupported(oper);
        changeCaretPosition(oper, position, 0);
    }

    @Override
    public void selectText(ComponentOperator oper, int startPosition, int finalPosition) {
        changeCaretPosition(oper, startPosition);
        DriverManager.newInstance(JemmyContext.getInstance()).getKeyDriver(oper).pressKey(oper, KeyEvent.VK_SHIFT, 0);
        changeCaretPosition(oper, finalPosition, InputEvent.SHIFT_MASK);
        DriverManager.newInstance(JemmyContext.getInstance()).getKeyDriver(oper).releaseKey(oper, KeyEvent.VK_SHIFT, 0);
    }

    @Override
    public void clearText(ComponentOperator oper) {
        DriverManager.newInstance(JemmyContext.getInstance())
                .getFocusDriver(oper)
                .giveFocus(oper);
        checkSupported(oper);
        KeyDriver kdriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getKeyDriver(oper);
        TimeoutKey betweenTime = getBetweenTimeout(oper);
        while (getCaretPosition(oper) > 0) {
            kdriver.typeKey(
                    oper,
                    KeyEvent.VK_BACK_SPACE,
                    (char) KeyEvent.VK_BACK_SPACE,
                    0,
                    TimeoutKey.ComponentOperator_PushKeyTimeout);
            Timeouts.sleep(betweenTime);
        }

        while (!getText(oper).isEmpty()) {
            kdriver.pushKey(oper, KeyEvent.VK_DELETE, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
            Timeouts.sleep(betweenTime);
        }
    }

    @Override
    public void typeText(ComponentOperator oper, String text, int caretPosition) {
        changeCaretPosition(oper, caretPosition);
        KeyDriver kDriver =
                DriverManager.newInstance(JemmyContext.getInstance()).getKeyDriver(oper);
        CharBindingMap map = oper.getCharBindingMap();
        TimeoutKey betweenTime = getBetweenTimeout(oper);
        char[] crs = text.toCharArray();
        for (char cr : crs) {
            kDriver.typeKey(
                    oper,
                    map.getCharKey(cr),
                    cr,
                    map.getCharModifiers(cr),
                    TimeoutKey.ComponentOperator_PushKeyTimeout);
            Timeouts.sleep(betweenTime);
        }
    }

    @Override
    public void changeText(ComponentOperator oper, String text) {
        clearText(oper);
        typeText(oper, text, 0);
    }

    @Override
    public void enterText(ComponentOperator oper, String text) {
        changeText(oper, text);
        DriverManager.newInstance(JemmyContext.getInstance())
                .getKeyDriver(oper)
                .pushKey(oper, KeyEvent.VK_ENTER, 0, TimeoutKey.TextKeyboardDriver_EnterText);
    }

    public abstract String getText(ComponentOperator oper);

    public abstract int getCaretPosition(ComponentOperator oper);

    public abstract int getSelectionStart(ComponentOperator oper);

    public abstract int getSelectionEnd(ComponentOperator oper);

    public abstract NavigationKey[] getKeys(ComponentOperator oper);

    public abstract TimeoutKey getBetweenTimeout(ComponentOperator oper);

    protected void changeCaretPosition(ComponentOperator oper, int position, int preModifiers) {
        NavigationKey[] keys = getKeys(oper);
        for (int i = keys.length - 1; i >= 0; i--) {
            if (keys[i] instanceof OffsetKey) {
                moveCaret(oper, (OffsetKey) keys[i], position, preModifiers);
            } else {
                moveCaret(oper, (GoAndBackKey) keys[i], position, preModifiers);
            }
        }
    }

    private int difference(int one, int two) {
        if (one >= two) {
            return one - two;
        } else {
            return two - one;
        }
    }

    private void push(ComponentOperator oper, NavigationKey key, int preModifiers) {
        DriverManager.newInstance(JemmyContext.getInstance())
                .getKeyDriver(oper)
                .pushKey(
                        oper,
                        key.getKeyCode(),
                        key.getModifiers() | preModifiers,
                        TimeoutKey.ComponentOperator_PushKeyTimeout);
        Timeouts.sleep(getBetweenTimeout(oper));
    }

    private void moveCaret(ComponentOperator oper, GoAndBackKey key, int position, int preModifiers) {
        int newDiff = difference(position, getCaretPosition(oper));
        int oldDiff = newDiff;
        while (key.getDirection() * (position - getCaretPosition(oper)) > 0) {
            oldDiff = newDiff;
            push(oper, key, preModifiers);
            newDiff = difference(position, getCaretPosition(oper));

            if (newDiff == oldDiff) {
                return;
            }
        }

        if (newDiff > oldDiff) {
            push(oper, key.getBackKey(), preModifiers);
        }
    }

    private void moveCaret(ComponentOperator oper, OffsetKey key, int position, int preModifiers) {
        if (gotToGo(oper, position, key.getExpectedPosition())) {
            push(oper, key, preModifiers);
        }
    }

    private boolean gotToGo(ComponentOperator oper, int point, int offset) {
        return difference(point, offset) < difference(point, getCaretPosition(oper));
    }
}
