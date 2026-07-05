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

package org.netbeans.jemmy.drivers.input;

import java.awt.Component;
import java.awt.event.KeyEvent;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class KeyEventDriver extends EventDriver implements KeyDriver {
    @Override
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers) {
        pressKey(findNativeParent(oper.getSource()), keyCode, modifiers);
    }

    @Override
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers) {
        releaseKey(findNativeParent(oper.getSource()), keyCode, modifiers);
    }

    @Override
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, TimeoutKey pushTime) {
        Component nativeContainer = findNativeParent(oper.getSource());
        pressKey(nativeContainer, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        releaseKey(nativeContainer, keyCode, modifiers);
    }

    @Override
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, TimeoutKey pushTime) {
        Component nativeContainer = findNativeParent(oper.getSource());
        pressKey(nativeContainer, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        dispatchEvent(
                nativeContainer,
                new KeyEvent(
                        nativeContainer,
                        KeyEvent.KEY_TYPED,
                        System.currentTimeMillis(),
                        modifiers,
                        KeyEvent.VK_UNDEFINED,
                        keyChar));
        releaseKey(nativeContainer, keyCode, modifiers);
    }

    private void pressKey(Component nativeContainer, int keyCode, int modifiers) {
        dispatchEvent(
                nativeContainer,
                new KeyEvent(nativeContainer, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), modifiers, keyCode));
    }

    private void releaseKey(Component nativeContainer, int keyCode, int modifiers) {
        dispatchEvent(
                nativeContainer,
                new KeyEvent(nativeContainer, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), modifiers, keyCode));
    }

    private Component findNativeParent(Component source) {
        Component nativeOne = source;
        while (nativeOne != null) {
            if (!nativeOne.isLightweight()) {
                return nativeOne;
            }

            nativeOne = nativeOne.getParent();
        }

        return source;
    }
}
