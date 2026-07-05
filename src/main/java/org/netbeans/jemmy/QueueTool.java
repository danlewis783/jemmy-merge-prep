package org.netbeans.jemmy;


import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.callables.CallablesJ;
import org.netbeans.jemmy.functions.StayingEmptyFunction;
import org.netbeans.jemmy.functions.SystemEventQueueEmptyFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InvocationEvent;
import java.util.concurrent.TimeUnit;

public final class QueueTool {
    private static final Logger logger = LoggerFactory.getLogger(QueueTool.class);

    private QueueTool() {}

    public void waitEmpty() {
        try {
            FunctionRepeater.on(new SystemEventQueueEmptyFunction(),
                    TimeoutKey.QueueTool_WaitQueueEmptyTimeout, TimeoutKey.QueueTool_QueueCheckingDelta).runUntilNotNull(null);} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void waitEmpty(long emptyTime) {
        StayingEmptyFunction waitable = new StayingEmptyFunction(emptyTime);
        FunctionRepeater<Void, Boolean> functionRepeater = FunctionRepeater.on(waitable,
                TimeoutKey.QueueTool_WaitQueueEmptyTimeout, TimeoutKey.QueueTool_QueueCheckingDelta);
        try {
            if (!functionRepeater.runUntilNotNull(null)) {
                logger.warn("functionRepeater did not return true");
            }
        } catch (TimeoutExpiredException e) {
            AWTEvent event = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent();
            if (event == null) {
                logger.warn(
                    "Timeout expired waiting for event queue to stay empty.  No event at front of event queue.  Consider increase of timeout.",
                    e);
            } else {
                if (logger.isWarnEnabled()) {
                    final String eventAtFrontOfQueue = invokeSmoothly(Caller.of(CallablesJ.toStringOf(event)));
                    logger.warn("Timeout expired waiting for event queue to stay empty.  Event at front of event queue: <{}>", eventAtFrontOfQueue, e);
                }
            }

            throw e;} catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T invokeSmoothly(Caller<T> caller) {
        if (!EventQueue.isDispatchThread()) {
            return invokeAndWait(caller);
        } else {
            try {
                return caller.getCallable().call();
            } catch (Exception e) {
                throw new JemmyException("Exception when calling", e);
            }
        }
    }

    private <T> T invokeAndWait(Caller<T> caller) {
        if (EventQueue.isDispatchThread()) {
            throw new Error("Cannot call invokeAndWait from the event dispatcher thread");
        }

        long preInvocationTimeout = Timeouts.get(TimeoutKey.QueueTool_PreInvocationTimeout);
        long invocationTimeout = Timeouts.get(TimeoutKey.QueueTool_InvocationTimeout);
        InvocationEvent event = new InvocationEvent(Toolkit.getDefaultToolkit(), caller, null, true);
        QueueUtils.processEvent(event);    

        try {
            if (!caller.getStartGate().await(preInvocationTimeout, TimeUnit.MILLISECONDS)) {
                throw new TimeoutExpiredException(
                    String.format(
                        "QueueTool_PreInvocationTimeout timeout (%d) exceeded waiting for start latch of caller",
                        preInvocationTimeout));
            }
        } catch (InterruptedException e) {
            throw new JemmyException("InterruptedException raised while waiting for start latch of caller", e);
        }

        try {
            if (!caller.getEndGate().await(invocationTimeout, TimeUnit.MILLISECONDS)) {
                throw new TimeoutExpiredException(
                    String.format(
                        "QueueTool_InvocationTimeout timeout (%d) exceeded waiting for end latch of caller",
                        invocationTimeout));
            }
        } catch (InterruptedException e) {
            throw new JemmyException("InterruptedException raised while waiting for end latch of caller", e);
        }

        Exception e = caller.getException();
        if (e != null) {
            throw new JemmyException("Exception captured by caller", e);
        }

        Throwable t = event.getThrowable();
        if (t != null) {
            throw new JemmyException("Throwable captured by invocation event", t);
        }

        return caller.getResult();
    }

    public static QueueTool getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final QueueTool INSTANCE = new QueueTool();
    }
}
