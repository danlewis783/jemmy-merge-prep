
package org.netbeans.jemmy.util;

import org.jspecify.annotations.Nullable;

import java.util.regex.Pattern;

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

    private static class AlwaysEqualStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null;
        }
    }


    private static class CaseInsensitiveStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && expected.equalsIgnoreCase(observed);
        }
    }


    private static class CaseInsensitiveSubstringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && observed.toUpperCase().contains(expected.toUpperCase());
        }
    }


    private static class RegexStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && Pattern.matches(expected, observed);
        }
    }


    private static class StrictStringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && expected.equals(observed);
        }
    }


    private static class SubstringComparator implements StringComparator {
        @Override
        public boolean equals(@Nullable String observed, @Nullable String expected) {
            return expected == null || observed != null && observed.contains(expected);
        }
    }
}
