/*
 * Copyright (c) 1997, 2017, Oracle and/or its affiliates. All rights reserved.
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

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global timeout registry, keyed by {@link TimeoutKey}.
 * <p>
 * Unlike upstream Jemmy, this fork intentionally omits the global timeout scaling feature (the
 * {@code jemmy.timeouts.scale} system property). A process-wide multiplier silently distorts every wait —
 * including explicit {@link #override(TimeoutKey, long)} values — which makes timing behavior
 * environment-dependent and failures hard to reproduce. Adjust the specific timeout with
 * {@link #override(TimeoutKey, long)} instead.
 */
public final class Timeouts {
    private static final Logger logger = LoggerFactory.getLogger(Timeouts.class);
    private final Map<TimeoutKey, TimeoutOverride> overrideMap;

    private Timeouts() {
        overrideMap = new EnumMap<>(TimeoutKey.class);
    }

    private synchronized void doResetToDefaults() {
        Set<TimeoutKey> timeoutKeys = overrideMap.keySet();
        for (TimeoutKey timeoutKey : timeoutKeys) {
            logger.warn("timeout \"{}\" was overridden before reset", timeoutKey);
        }
        overrideMap.clear();
    }

    public static void resetToDefaults() {
        getInstance().doResetToDefaults();
    }

    private synchronized long doGet(TimeoutKey key) {
        TimeoutOverride timeoutOverride = overrideMap.get(key);
        return (timeoutOverride != null) ? timeoutOverride.get() : key.getDefaultValue();
    }

    private synchronized TimeoutOverride doOverride(TimeoutKey key, long newValue) {
        if (overrideMap.containsKey(key)) {
            throw new IllegalStateException(String.format("override of \"%s\" failed because already overridden", key));
        }
        if (newValue == key.getDefaultValue()) {
            throw new IllegalArgumentException(String.format(
                    "override of \"%s\" failed because new value (%d ms) same as default", key, newValue));
        }
        TimeoutOverride ret = new TimeoutOverrideImpl(key, newValue);
        overrideMap.put(key, ret);
        return ret;
    }

    public static long get(TimeoutKey key) {
        Objects.requireNonNull(key, "key");
        return getInstance().doGet(key);
    }

    public static void check(TimeoutKey key, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        long timeoutValue = get(key);
        if (elapsed > timeoutValue) {
            throw new TimeoutExpiredException(
                    String.format("timeout \"%s\" (%d ms) exceeded after (%d ms)", key, timeoutValue, elapsed));
        }
    }

    public static TimeoutOverride override(TimeoutKey key, long newValue) {
        Objects.requireNonNull(key, "key");
        if (newValue < 0L) {
            throw new IllegalArgumentException(String.format(
                    "attempt to override \"%s\" failed because new value (%d ms) must be non-negative", key, newValue));
        }
        return getInstance().doOverride(key, newValue);
    }

    public static void sleep(TimeoutKey key) {
        long value = get(key);
        try {
            if (value == 0) {
                return;
            }

            Thread.sleep(value);
        } catch (InterruptedException e) {
            throw new JemmyException(
                    String.format("interrupted while sleeping for timeout \"%s\" (%d ms)", key, value), e);
        }
    }

    private static Timeouts getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final Timeouts INSTANCE = new Timeouts();
    }

    private class TimeoutOverrideImpl implements TimeoutOverride {
        private final TimeoutKey key;
        private final long value;

        public TimeoutOverrideImpl(TimeoutKey key, long value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public void cancel() {
            synchronized (Timeouts.this) {
                //noinspection resource
                final TimeoutOverride prev = overrideMap.remove(key);
                if (prev == null) {
                    logger.debug("cancel of timeout \"{}\" unnecessary because not currently overridden", key);
                }
            }
        }

        @Override
        public long get() {
            return value;
        }

        @Override
        public TimeoutKey key() {
            return key;
        }

        @Override
        public void close() {
            cancel();
        }
    }
}
