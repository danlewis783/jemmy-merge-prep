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
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import org.netbeans.jemmy.callables.Callables;
import org.netbeans.jemmy.functions.StayingEmptyBooleanSupplier;
import org.netbeans.jemmy.functions.SystemEventQueueEmptyBooleanSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QueueTool {
    private static final Logger logger = LoggerFactory.getLogger(QueueTool.class);

    private QueueTool() {}

    /**
     * Wait for the event queue to be empty when sampled.
     */
    public void waitEmpty() {
        BooleanSupplierRepeater.on(
                        new SystemEventQueueEmptyBooleanSupplier(),
                        TimeoutKey.QueueTool_WaitQueueEmptyTimeout,
                        TimeoutKey.QueueTool_QueueCheckingDelta)
                .runUntilTrue();
    }

    /**
     * Wait for the event queue to <em>stay</em> empty, when sampled, for {@code emptyTimeMs} milliseconds.
     *
     * @param emptyTimeMs the amount of time the event queue should stay empty
     */
    public void waitEmpty(long emptyTimeMs) {
        BooleanSupplierRepeater supplierRepeater = BooleanSupplierRepeater.on(
                new StayingEmptyBooleanSupplier(emptyTimeMs),
                TimeoutKey.QueueTool_WaitQueueEmptyTimeout,
                TimeoutKey.QueueTool_QueueCheckingDelta);
        try {
            supplierRepeater.runUntilTrue();
        } catch (TimeoutExpiredException e) {
            AWTEvent event = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent();
            if (event == null) {
                logger.warn(
                        "Timeout expired waiting for event queue to stay empty.  No event at front of event queue.  Consider increase of timeout.",
                        e);
            } else {
                if (logger.isWarnEnabled()) {
                    final String eventAtFrontOfQueue = callOnQueue(Callables.toStringOf(event));
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
     * Calls the callable on the AWT event dispatch thread and blocks until it completes, returning
     * its result. When invoked off the dispatch thread, the call is dispatched through the system
     * event queue and waited on (subject to {@code QueueTool_PreInvocationTimeout} and
     * {@code QueueTool_InvocationTimeout}); when already on the dispatch thread, the callable runs
     * directly. Any exception the callable throws is rethrown wrapped in a {@link JemmyException},
     * except that a thrown {@link JemmyException} propagates as-is - work that pre-wraps a checked
     * exception keeps a single wrapper, so {@code getCause()} stays the original exception.
     *
     * @param callable the work to run on the dispatch thread
     * @return the callable's result; null exactly when the callable returns null
     */
    public <R> R callOnQueue(Callable<R> callable) {
        if (EventQueue.isDispatchThread()) {
            try {
                return callable.call();
            } catch (JemmyException e) {
                throw e;
            } catch (Exception e) {
                throw new JemmyException("Exception when calling", e);
            }
        }

        return invokeAndWait(Caller.of(callable));
    }

    public boolean callOnQueue(BooleanSupplier booleanSupplier) {
        return callOnQueue(Callables.fromBooleanSupplier(booleanSupplier));
    }

    /**
     * Runs the runnable on the AWT event dispatch thread and blocks until it completes, with the
     * same dispatch, timeout, and exception-wrapping behavior as {@link #callOnQueue}.
     *
     * @param runnable the work to run on the dispatch thread
     */
    public void runOnQueue(Runnable runnable) {
        if (EventQueue.isDispatchThread()) {
            try {
                runnable.run();
            } catch (JemmyException e) {
                throw e;
            } catch (RuntimeException e) {
                throw new JemmyException("Exception when calling", e);
            }
            return;
        }

        dispatchAndAwait(Caller.of(() -> {
            runnable.run();
            return null;
        }));
    }

    // the result's nullness follows the Caller's type argument,
    // which pre-generics NullAway cannot express
    @SuppressWarnings("NullAway")
    private <R> R invokeAndWait(Caller<R> caller) {
        dispatchAndAwait(caller);
        return caller.getResult();
    }

    private void dispatchAndAwait(Caller<?> caller) {
        if (EventQueue.isDispatchThread()) {
            throw new Error("Cannot dispatch and await from the event dispatcher thread");
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

        Throwable thrown = caller.getThrowable();
        if (thrown instanceof JemmyException) {
            throw (JemmyException) thrown;
        }
        if (thrown != null) {
            throw new JemmyException("Throwable captured by caller", thrown);
        }

        // backstop for throwables raised outside the caller's capture; unlike the caller's
        // gate-ordered field, this read is not synchronized with the end gate - best effort only
        Throwable t = event.getThrowable();
        if (t != null) {
            throw new JemmyException("Throwable captured by invocation event", t);
        }
    }

    public static QueueTool getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final QueueTool INSTANCE = new QueueTool();
    }
}
