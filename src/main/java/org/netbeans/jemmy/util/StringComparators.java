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

public final class StringComparators {
    private StringComparators() {}

    public static StringComparator strict() {
        return new StrictStringComparator();
    }

    public static StringComparator regex() {
        return new RegexStringComparator();
    }

    public static StringComparator caseInsensitive() {
        return new CaseInsensitiveStringComparator();
    }

    public static StringComparator substring() {
        return new SubstringComparator();
    }

    public static StringComparator caseInsensitiveSubstring() {
        return new CaseInsensitiveSubstringComparator();
    }

    public static StringComparator alwaysEqual() {
        return new AlwaysEqualStringComparator();
    }

    public static StringComparator startsWith() {
        return new StartsWithStringComparator();
    }

    public static StringComparator trimming() {
        return new TrimmingStringComparator();
    }

    private static class AlwaysEqualStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null;
        }

        @Override
        public String toString() {
            return "AlwaysEqualStringComparator";
        }
    }

    private static class CaseInsensitiveStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || expected.equalsIgnoreCase(observed);
        }

        @Override
        public String toString() {
            return "CaseInsensitiveStringComparator";
        }
    }

    private static class CaseInsensitiveSubstringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            if (expected == null) {
                return true;
            }
            if (observed == null) {
                return false;
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
            return "CaseInsensitiveSubstringComparator";
        }
    }

    private static class RegexStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && Pattern.matches(expected, observed);
        }

        @Override
        public String toString() {
            return "RegexStringComparator";
        }
    }

    private static class StartsWithStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && observed.startsWith(expected);
        }

        @Override
        public String toString() {
            return "StartsWithStringComparator";
        }
    }

    private static class StrictStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || expected.equals(observed);
        }

        @Override
        public String toString() {
            return "StrictStringComparator";
        }
    }

    private static class TrimmingStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && expected.equals(observed.trim());
        }

        @Override
        public String toString() {
            return "TrimmingStringComparator";
        }
    }

    private static class SubstringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && observed.contains(expected);
        }

        @Override
        public String toString() {
            return "SubstringComparator";
        }
    }
}
