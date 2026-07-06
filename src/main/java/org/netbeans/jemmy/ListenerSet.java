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
import java.awt.Toolkit;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ListenerSet {
    private static final Logger logger = LoggerFactory.getLogger(ListenerSet.class);

    private final Set<EventListener> listeners;
    private final long theWholeMask;

    private ListenerSet() {
        Set<EventListener> eventListenerSet = new HashSet<>();
        long theWholeMask;
        try {
            Field[] fields = AWTEvent.class.getFields();
            theWholeMask = 0;
            long eventMask;
            for (Field field : fields) {
                if ((field.getModifiers() & (Modifier.PUBLIC | Modifier.STATIC)) != 0
                        && field.getType().equals(Long.TYPE)
                        && field.getName().endsWith("_EVENT_MASK")) {
                    eventMask = (Long) field.get(null);
                    eventListenerSet.add(new EventListener(eventMask));
                    theWholeMask = theWholeMask | eventMask;
                }
            }
        } catch (IllegalAccessException e) {
            logger.warn("", e);
            throw new RuntimeException(e);
        }

        this.theWholeMask = theWholeMask;
        listeners = Collections.unmodifiableSet(eventListenerSet);
    }

    public void addListeners(long eventMask) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (EventListener listener : listeners) {
            if ((listener.getEventMask() & eventMask) != 0) {
                toolkit.addAWTEventListener(listener, listener.getEventMask());
            }
        }
    }

    public void addListeners() {
        addListeners(getTheWholeMask());
    }

    public void removeListeners() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        for (EventListener listener : listeners) {
            toolkit.removeAWTEventListener(listener);
        }
    }

    public long getTheWholeMask() {
        return theWholeMask;
    }

    public long getLastEventTime(long eventMask) {
        EventListener et = getLastEventType(eventMask);

        return (et == null) ? -1 : et.getTime();
    }

    @Nullable
    public AWTEvent getLastEvent(long eventMask) {
        EventListener et = getLastEventType(eventMask);

        return (et == null) ? null : et.getEvent();
    }

    @Nullable
    public EventListener getLastEventType(long eventMask) {
        long maxTime = -1;
        EventListener maxType = null;
        for (EventListener listener : listeners) {
            if ((eventMask & listener.getEventMask()) != 0 && (listener.getTime() > maxTime)) {
                maxType = listener;
                maxTime = maxType.getTime();
            }
        }

        return maxType;
    }

    public static ListenerSet getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ListenerSet INSTANCE = new ListenerSet();
    }
}
