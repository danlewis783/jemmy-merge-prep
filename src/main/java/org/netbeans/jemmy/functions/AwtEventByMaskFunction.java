/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.functions;

import java.awt.AWTEvent;
import java.util.function.Function;
import org.netbeans.jemmy.EventListener;
import org.netbeans.jemmy.EventTool;

public final class AwtEventByMaskFunction implements Function<Void, AWTEvent> {
    private final long eventMask;
    private final long startTime;

    public AwtEventByMaskFunction(long eventMask) {
        this.eventMask = eventMask;
        startTime = EventTool.getInstance().getLastEventTime(eventMask);
    }

    @Override
    public AWTEvent apply(Void obj) {
        EventListener eventListener = EventTool.getInstance().getListenerSet().getLastEventType(eventMask);
        if ((eventListener != null) && (eventListener.getTime() > startTime)) {
            return eventListener.getEvent();
        } else {
            return null;
        }
    }
}
