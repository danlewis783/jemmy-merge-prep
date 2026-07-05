package org.netbeans.jemmy.functions;

import java.util.function.Function;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.TimeoutKey;

public final class NoEventFunction implements Function<Void, Boolean> {
    private final long eventMask;
    private final EventTool eventTool;
    private final TimeoutKey waitTime;

    public NoEventFunction(long eventMask, TimeoutKey waitTime, EventTool eventTool) {
        this.eventMask = eventMask;
        this.waitTime = waitTime;
        this.eventTool = eventTool;
    }

    @Override
    public Boolean apply(Void obj) {
        return eventTool.checkNoEvent(eventMask, waitTime) ? true : null;
    }
}
