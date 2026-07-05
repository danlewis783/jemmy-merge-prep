package org.netbeans.jemmy.util;

import org.jspecify.annotations.Nullable;

public interface StringComparator {
    /**
     * @param observed possibly null observed value
     * @param expected possibly null expected value
     * @return true if strings are "equal" according to comparator; true if expected value is null, false if observed value is null
     */
    public boolean equals(@Nullable String observed, @Nullable String expected);
}
