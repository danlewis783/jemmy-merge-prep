
package org.netbeans.jemmy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class Timeouts {
    private static final Logger logger = LoggerFactory.getLogger(Timeouts.class);
    private final Map<TimeoutKey, TimeoutOverride> overrideMap;

    private Timeouts() {
        overrideMap = new EnumMap<>(TimeoutKey.class);

        doResetToDefaults();
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
        if (timeoutOverride != null) {
            return timeoutOverride.get();
        }

        return key.getDefaultValue();
    }

    private synchronized TimeoutOverride doOverride(TimeoutKey key, long newValue) {
        if (overrideMap.containsKey(key)) {
            throw new IllegalStateException(String.format("override of \"%s\" failed because already overridden", key));
        }
        assert newValue != key.getDefaultValue() : String.format(
                "override of \"%s\" failed because new value (%d ms) same as default", key, newValue);
        TimeoutOverride ret = new TimeoutOverrideImpl(key, newValue);
        overrideMap.put(key, ret);
        return ret;
    }

    public static long get(TimeoutKey key) {
        Objects.requireNonNull(key, "key must not be null");
        return getInstance().doGet(key);
    }

    public static void check(TimeoutKey key, long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        long timeoutValue = get(key);
        if (elapsed > timeoutValue) {
            throw new TimeoutExpiredException(
                String.format(
                    "timeout \"%s\" (%d ms) exceeded after (%d ms)", key, timeoutValue, elapsed));
        }
    }

    public static TimeoutOverride override(TimeoutKey key, long newValue) {
        Objects.requireNonNull(key, "key must not be null");
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
            throw new JemmyException(String.format("interrupted while sleeping for timeout \"%s\" (%d ms)", key,
                    value), e);
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
