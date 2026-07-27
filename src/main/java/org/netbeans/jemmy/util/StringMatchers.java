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

import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

/**
 * Factory methods for common string matching strategies.
 *
 * <p>Except for {@link #always()}, the matchers treat null as an ordinary absent value: two null values match, while
 * a null value does not match a non-null value. Composed matchers follow ordinary boolean logic and are not required
 * to retain that factory convention.
 */
public final class StringMatchers {
    private StringMatchers() {}

    public static StringMatcher strict() {
        return new StrictStringMatcher();
    }

    public static StringMatcher regex() {
        return new RegexStringMatcher();
    }

    public static StringMatcher caseInsensitive() {
        return new CaseInsensitiveStringMatcher();
    }

    public static StringMatcher substring() {
        return new SubstringMatcher();
    }

    public static StringMatcher caseInsensitiveSubstring() {
        return new CaseInsensitiveSubstringMatcher();
    }

    public static StringMatcher always() {
        return new AlwaysStringMatcher();
    }

    public static StringMatcher startsWith() {
        return new StartsWithStringMatcher();
    }

    private static final class AlwaysStringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            return true;
        }

        @Override
        public String toString() {
            return "AlwaysStringMatcher";
        }
    }

    private static final class CaseInsensitiveStringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            return expected.equalsIgnoreCase(observed);
        }

        @Override
        public String toString() {
            return "CaseInsensitiveStringMatcher";
        }
    }

    private static final class CaseInsensitiveSubstringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            int lastStart = observed.length() - expected.length();
            for (int start = 0; start <= lastStart; start++) {
                if (observed.regionMatches(true, start, expected, 0, expected.length())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return "CaseInsensitiveSubstringMatcher";
        }
    }

    private static final class RegexStringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            return Pattern.matches(expected, observed);
        }

        @Override
        public String toString() {
            return "RegexStringMatcher";
        }
    }

    private static final class StartsWithStringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            return observed.startsWith(expected);
        }

        @Override
        public String toString() {
            return "StartsWithStringMatcher";
        }
    }

    private static final class StrictStringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            return expected.equals(observed);
        }

        @Override
        public String toString() {
            return "StrictStringMatcher";
        }
    }

    private static final class SubstringMatcher implements StringMatcher {
        @Override
        public boolean test(@Nullable String observed, @Nullable String expected) {
            if (observed == null || expected == null) {
                return observed == expected;
            }
            return observed.contains(expected);
        }

        @Override
        public String toString() {
            return "SubstringMatcher";
        }
    }
}
