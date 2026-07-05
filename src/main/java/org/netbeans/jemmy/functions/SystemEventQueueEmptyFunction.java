package org.netbeans.jemmy.functions;

import java.util.function.Function;

import java.awt.*;

public final class SystemEventQueueEmptyFunction implements Function<Void, Boolean> {
    @Override
    public Boolean apply(Void obj) {
        return (Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null) ? true : null;
    }
}
