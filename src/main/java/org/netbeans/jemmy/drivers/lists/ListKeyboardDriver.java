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

package org.netbeans.jemmy.drivers.lists;

import java.awt.event.KeyEvent;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.FocusDriver;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.drivers.MultiSelListDriver;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.ListOperator;

public final class ListKeyboardDriver extends ListAPIDriver implements MultiSelListDriver {
    public ListKeyboardDriver() {}

    @Override
    public void selectItem(ComponentOperator compOp, int index) {
        ListOperator listOp = (ListOperator) compOp;
        if (listOp.isMultipleMode()) {
            super.selectItem(listOp, index);
        }

        DriverManager driverManager = DriverManager.newInstance(JemmyProperties.getInstance());
        FocusDriver focusDriver = driverManager.getFocusDriver(compOp);
        focusDriver.giveFocus(compOp);
        KeyDriver keyDriver = driverManager.getKeyDriver(compOp);
        int current = listOp.getSelectedIndex();
        int diff;
        int key;
        if (index > current) {
            diff = index - current;
            key = KeyEvent.VK_DOWN;
        } else {
            diff = current - index;
            key = KeyEvent.VK_UP;
        }

        for (int i = 0; i < diff; i++) {
            keyDriver.pushKey(compOp, key, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
        }

        keyDriver.pushKey(compOp, KeyEvent.VK_ENTER, 0, TimeoutKey.ComponentOperator_PushKeyTimeout);
    }
}
