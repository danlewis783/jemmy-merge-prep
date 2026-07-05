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

import java.util.List;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.drivers.KeyDriver;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class KeyRobotDriver extends RobotDriver implements KeyDriver {
    public KeyRobotDriver(TimeoutKey robotAutoDelay) {
        super(robotAutoDelay);
    }

    public KeyRobotDriver(TimeoutKey robotAutoDelay, List<String> supported) {
        super(robotAutoDelay, supported);
    }

    @Override
    public void pushKey(ComponentOperator oper, int keyCode, int modifiers, TimeoutKey pushTime) {
        pressKey(oper, keyCode, modifiers);
        Timeouts.sleep(pushTime);
        releaseKey(oper, keyCode, modifiers);
    }

    @Override
    public void typeKey(ComponentOperator oper, int keyCode, char keyChar, int modifiers, TimeoutKey pushTime) {
        pushKey(oper, keyCode, modifiers, pushTime);
    }

    @Override
    public void pressKey(ComponentOperator oper, int keyCode, int modifiers) {
        pressKey(keyCode, modifiers);
    }

    @Override
    public void releaseKey(ComponentOperator oper, int keyCode, int modifiers) {
        releaseKey(keyCode, modifiers);
    }
}
