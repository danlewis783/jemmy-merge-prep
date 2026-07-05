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
package org.netbeans.jemmy;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicLong;

public final class EventListener implements AWTEventListener {
    private final long eventMask;
    private Reference<AWTEvent> eventRef;
    private final AtomicLong eventTime;

    public EventListener(long eventMask) {
        this.eventMask = eventMask;
        eventRef = new WeakReference<>(null);
        eventTime = new AtomicLong(-1);
    }

    @Override
    public void eventDispatched(AWTEvent event) {
        eventRef = new WeakReference<>(event);
        eventTime.set(System.currentTimeMillis());
    }

    public AWTEvent getEvent() {
        return eventRef.get();
    }

    public long getTime() {
        return eventTime.get();
    }

    public long getEventMask() {
        return eventMask;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof EventListener)) {
            return false;
        }

        EventListener eventListener = (EventListener) obj;

        return eventListener.eventMask == eventMask;
    }

    @Override
    public int hashCode() {
        return (int) (eventMask ^ (eventMask >>> 32));
    }
}
