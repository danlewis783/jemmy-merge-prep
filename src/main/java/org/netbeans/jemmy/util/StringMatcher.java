/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.util;

import java.util.Objects;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

/**
 * A composable predicate over an observed string and an expected value or pattern.
 * <p>
 * This interface adds no special null or wildcard rules to {@link BiPredicate}. Individual matchers define how
 * they handle null inputs, and logical composition follows the normal {@code BiPredicate} truth tables.
 */
@FunctionalInterface
public interface StringMatcher extends BiPredicate<String, String> {

    /**
     * Determines whether an observed string matches an expected value.
     *
     * @param observed the string being examined
     * @param expected the value or pattern to match
     * @return whether the observed string matches the expected value
     */
    @Override
    boolean test(@Nullable String observed, @Nullable String expected);

    @Override
    default StringMatcher and(BiPredicate<? super String, ? super String> other) {
        Objects.requireNonNull(other);
        return (observed, expected) -> test(observed, expected) && other.test(observed, expected);
    }

    @Override
    default StringMatcher negate() {
        return (observed, expected) -> !test(observed, expected);
    }

    @Override
    default StringMatcher or(BiPredicate<? super String, ? super String> other) {
        Objects.requireNonNull(other);
        return (observed, expected) -> test(observed, expected) || other.test(observed, expected);
    }
}
