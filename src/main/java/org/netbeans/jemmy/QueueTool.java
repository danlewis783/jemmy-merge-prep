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
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.util.concurrent.TimeUnit;
import org.netbeans.jemmy.callables.Callables;
import org.netbeans.jemmy.functions.StayingEmptyFunction;
import org.netbeans.jemmy.functions.SystemEventQueueEmptyFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QueueTool {
    private static final Logger logger = LoggerFactory.getLogger(QueueTool.class);

    private QueueTool() {}

    public void waitEmpty() {
        FunctionRepeater.on(
                        new SystemEventQueueEmptyFunction(),
                        TimeoutKey.QueueTool_WaitQueueEmptyTimeout,
                        TimeoutKey.QueueTool_QueueCheckingDelta)
                .runUntilNotNull(null);
    }

    public void waitEmpty(long emptyTime) {
        FunctionRepeater<Void, Boolean> functionRepeater = FunctionRepeater.on(
                new StayingEmptyFunction(emptyTime),
                TimeoutKey.QueueTool_WaitQueueEmptyTimeout,
                TimeoutKey.QueueTool_QueueCheckingDelta);
        try {
            functionRepeater.runUntilNotNull(null);
        } catch (TimeoutExpiredException e) {
            AWTEvent event = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent();
            if (event == null) {
                logger.warn(
                        "Timeout expired waiting for event queue to stay empty.  No event at front of event queue.  Consider increase of timeout.",
                        e);
            } else {
                if (logger.isWarnEnabled()) {
                    final String eventAtFrontOfQueue = callOnQueue(Caller.of(Callables.toStringOf(event)));
                    logger.warn(
                            "Timeout expired waiting for event queue to stay empty.  Event at front of event queue: <{}>",
                            eventAtFrontOfQueue,
                            e);
                }
            }

            throw e;
        }
    }

    /**
     * Calls the caller's callable on the AWT event dispatch thread and blocks until it completes,
     * returning its result. When invoked off the dispatch thread, the call is dispatched through
     * the system event queue and waited on (subject to {@code QueueTool_PreInvocationTimeout} and
     * {@code QueueTool_InvocationTimeout}); when already on the dispatch thread, the callable runs
     * directly. Any exception the callable throws is rethrown wrapped in a {@link JemmyException}.
     *
     * @param caller wraps the callable to run; single-use, see {@link Caller#of}
     * @return the callable's result
     */
    public <T> T callOnQueue(Caller<T> caller) {
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

    // the result's nullness follows the Caller's type argument (null for Callable<Void>),
    // which pre-generics NullAway cannot express
    @SuppressWarnings("NullAway")
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
                throw new TimeoutExpiredException(String.format(
                        "QueueTool_PreInvocationTimeout timeout (%d) exceeded waiting for start latch of caller",
                        preInvocationTimeout));
            }
        } catch (InterruptedException e) {
            throw new JemmyException("InterruptedException raised while waiting for start latch of caller", e);
        }

        try {
            if (!caller.getEndGate().await(invocationTimeout, TimeUnit.MILLISECONDS)) {
                throw new TimeoutExpiredException(String.format(
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
