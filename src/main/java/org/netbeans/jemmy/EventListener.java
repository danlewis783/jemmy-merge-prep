package org.netbeans.jemmy;

import java.awt.*;
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
