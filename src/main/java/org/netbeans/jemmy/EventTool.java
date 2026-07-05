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
package org.netbeans.jemmy;

import java.awt.AWTEvent;
import org.netbeans.jemmy.functions.AwtEventByMaskFunction;
import org.netbeans.jemmy.functions.NoEventFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EventTool {
    private static final Logger logger = LoggerFactory.getLogger(EventTool.class);
    private long currentEventMask = 0;
    private final ListenerSet listenerSet;

    private EventTool() {
        listenerSet = ListenerSet.getInstance();
        String jemmy_event_listening = System.getProperty("jemmy.event_listening");
        if ((jemmy_event_listening == null) || !"no".equals(jemmy_event_listening)) {
            listenerSet.addListeners();
        }
    }

    public long getLastEventTime(long eventMask) {
        return listenerSet.getLastEventTime(eventMask);
    }

    public AWTEvent getLastEvent(long eventMask) {
        return listenerSet.getLastEvent(eventMask);
    }

    public AWTEvent getLastEvent() {
        return getLastEvent(listenerSet.getTheWholeMask());
    }

    public void addListeners(long eventMask) {
        removeListeners();
        listenerSet.addListeners(eventMask);
        currentEventMask = eventMask;
    }

    public void addListeners() {
        addListeners(listenerSet.getTheWholeMask());
    }

    public void removeListeners() {
        listenerSet.removeListeners();
    }

    public long getCurrentEventMask() {
        return currentEventMask;
    }

    public AWTEvent waitEvent(long eventMask) {
        return waitEvent(eventMask, TimeoutKey.EventTool_WaitEventTimeout);
    }

    public AWTEvent waitEvent() {
        return waitEvent(listenerSet.getTheWholeMask());
    }

    public boolean checkNoEvent() {
        return checkNoEvent(listenerSet.getTheWholeMask(), TimeoutKey.EventTool_WaitEventTimeout);
    }

    public boolean checkNoEvent(long eventMask) {
        return checkNoEvent(eventMask, TimeoutKey.EventTool_WaitEventTimeout);
    }

    public boolean checkNoEvent(TimeoutKey waitTime) {
        return checkNoEvent(listenerSet.getTheWholeMask(), waitTime);
    }

    public void waitNoEvent(long eventMask, TimeoutKey waitTime) {
        try {
            FunctionRepeater.on(
                            new NoEventFunction(eventMask, waitTime, this),
                            waitTime,
                            TimeoutKey.EventTool_EventCheckingDelta)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private AWTEvent waitEvent(long eventMask, TimeoutKey waitTime) {
        try {
            return FunctionRepeater.on(
                            new AwtEventByMaskFunction(eventMask), waitTime, TimeoutKey.EventTool_EventCheckingDelta)
                    .runUntilNotNull(null);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitNoEvent(long eventMask) {
        ListenerSet ls = listenerSet;
        if (ls != null) {

            // surprisingly this field can be null in case of massive
            // garbage collecting efforts like in NbTestCase.assertGC
            waitNoEvent(eventMask, TimeoutKey.EventTool_WaitEventTimeout);
        }
    }

    public void waitNoEvent(TimeoutKey waitTime) {
        ListenerSet ls = listenerSet;
        if (ls != null) {

            // surprisingly this field can be null in case of massive
            // garbage collecting efforts like in NbTestCase.assertGC
            waitNoEvent(ls.getTheWholeMask(), waitTime);
        }
    }

    public boolean checkNoEvent(long eventMask, TimeoutKey waitTime) {
        try {
            waitEvent(eventMask, waitTime);

            return false;
        } catch (TimeoutExpiredException e) {
            return true;
        }
    }

    public ListenerSet getListenerSet() {
        return listenerSet;
    }

    public static EventTool getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final EventTool INSTANCE = new EventTool();
    }
}
