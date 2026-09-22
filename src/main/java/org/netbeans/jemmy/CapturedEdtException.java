/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 */
package org.netbeans.jemmy;

import java.io.Serializable;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;

/** Concise and full representations of a secondary EDT failure. */
public final class CapturedEdtException implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String summary;
    private final String detail;
    private final Instant occurredAt;
    private final long elapsedNanos;
    private final String threadName;
    private final long threadId;
    private final String captureMechanism;
    private final @Nullable String invokingThreadName;
    private final long invokingThreadId;
    private final @Nullable String invocationDetail;

    CapturedEdtException(
            String summary,
            String detail,
            Instant occurredAt,
            long elapsedNanos,
            String threadName,
            long threadId,
            String captureMechanism,
            @Nullable String invokingThreadName,
            long invokingThreadId,
            @Nullable String invocationDetail) {
        this.summary = summary;
        this.detail = detail;
        this.occurredAt = occurredAt;
        this.elapsedNanos = elapsedNanos;
        this.threadName = threadName;
        this.threadId = threadId;
        this.captureMechanism = captureMechanism;
        this.invokingThreadName = invokingThreadName;
        this.invokingThreadId = invokingThreadId;
        this.invocationDetail = invocationDetail;
    }

    public String summary() {
        return summary;
    }

    public String detail() {
        return detail;
    }

    public Instant occurredAt() {
        return occurredAt;
    }

    public long elapsedNanos() {
        return elapsedNanos;
    }

    public String threadName() {
        return threadName;
    }

    public long threadId() {
        return threadId;
    }

    public String captureMechanism() {
        return captureMechanism;
    }

    public @Nullable String invokingThreadName() {
        return invokingThreadName;
    }

    public long invokingThreadId() {
        return invokingThreadId;
    }

    public @Nullable String invocationDetail() {
        return invocationDetail;
    }
}
