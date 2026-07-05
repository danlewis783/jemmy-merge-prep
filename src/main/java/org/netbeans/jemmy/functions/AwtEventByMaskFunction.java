package org.netbeans.jemmy.functions;

import java.util.function.Function;
import org.netbeans.jemmy.EventListener;
import org.netbeans.jemmy.EventTool;

import java.awt.*;

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
