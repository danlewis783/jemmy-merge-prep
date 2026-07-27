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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StringMatchersTest {
    static Stream<StringMatcher> valueMatchers() {
        return Stream.of(
                StringMatchers.strict(),
                StringMatchers.regex(),
                StringMatchers.caseInsensitive(),
                StringMatchers.substring(),
                StringMatchers.caseInsensitiveSubstring(),
                StringMatchers.startsWith());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("valueMatchers")
    void factoryMatchersTreatNullAsAnOrdinaryAbsentValue(StringMatcher matcher) {
        assertThat(matcher.test(null, null)).isTrue();
        assertThat(matcher.test("observed", null)).isFalse();
        assertThat(matcher.test(null, "expected")).isFalse();
    }

    @Test
    void strictMatchesEqualStringsOnly() {
        StringMatcher matcher = StringMatchers.strict();

        assertThat(matcher.test("Save", "Save")).isTrue();
        assertThat(matcher.test("Save", "save")).isFalse();
    }

    @Test
    void caseInsensitiveMatchesIgnoringCase() {
        StringMatcher matcher = StringMatchers.caseInsensitive();

        assertThat(matcher.test("Save", "sAvE")).isTrue();
        assertThat(matcher.test("Save", "Saved")).isFalse();
    }

    @Test
    void substringMatchesContainedText() {
        StringMatcher matcher = StringMatchers.substring();

        assertThat(matcher.test("Save As", "Save")).isTrue();
        assertThat(matcher.test("Save As", "save")).isFalse();
        assertThat(matcher.test("Save As", "Open")).isFalse();
    }

    @Test
    void caseInsensitiveSubstringIsLocaleIndependent() {
        Locale previousLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertThat(StringMatchers.caseInsensitiveSubstring().test("TITLE", "title")).isTrue();
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    @Test
    void regexMatchesTheEntireObservedString() {
        StringMatcher matcher = StringMatchers.regex();

        assertThat(matcher.test("seventeen", ".*ent.*")).isTrue();
        assertThat(matcher.test("seventeen", "ent")).isFalse();
    }

    @Test
    void alwaysMatchesEveryPairOfValues() {
        StringMatcher matcher = StringMatchers.always();

        assertThat(matcher.test("anything", "different")).isTrue();
        assertThat(matcher.test(null, "different")).isTrue();
        assertThat(matcher.test("anything", null)).isTrue();
        assertThat(matcher.test(null, null)).isTrue();
    }

    @Test
    void startsWithMatchesPrefixes() {
        StringMatcher matcher = StringMatchers.startsWith();

        assertThat(matcher.test("Save As", "Save")).isTrue();
        assertThat(matcher.test("Save As", "As")).isFalse();
    }

    @Test
    void isUsableAsABiPredicate() {
        BiPredicate<String, String> matcher = StringMatchers.strict();

        assertThat(matcher.test("Save", "Save")).isTrue();
        assertThat(matcher.test("Save", "Open")).isFalse();
    }

    @Test
    void andCanAddAnObservedTextConstraint() {
        // Match a contained label while rejecting UI text padded with unexpected whitespace.
        StringMatcher unpaddedCaseInsensitiveSubstring = StringMatchers.caseInsensitiveSubstring()
                .and((observed, expected) -> observed != null && observed.equals(observed.trim()));

        assertThat(unpaddedCaseInsensitiveSubstring.test("Save As", "save")).isTrue();
        assertThat(unpaddedCaseInsensitiveSubstring.test(" Save As ", "save")).isFalse();
    }

    @Test
    void orCanCombineAcceptedMatchingStrategies() {
        // Accept either a case-sensitive prefix or a complete label with different casing.
        StringMatcher prefixOrCaseInsensitiveExact =
                StringMatchers.startsWith().or(StringMatchers.caseInsensitive());

        assertThat(prefixOrCaseInsensitiveExact.test("Save As", "Save")).isTrue();
        assertThat(prefixOrCaseInsensitiveExact.test("save", "Save")).isTrue();
        assertThat(prefixOrCaseInsensitiveExact.test("save as", "Save")).isFalse();
    }

    @Test
    void negateCanExcludeMatchingText() {
        // Invert a matcher when filtering out labels containing an unwanted value.
        StringMatcher doesNotContain = StringMatchers.substring().negate();

        assertThat(doesNotContain.test("Save As", "Save")).isFalse();
        assertThat(doesNotContain.test("Open", "Save")).isTrue();
        assertThat(doesNotContain.test(null, null)).isFalse();
        assertThat(doesNotContain.test(null, "Save")).isTrue();
    }

    @Test
    void compositionRetainsBiPredicateShortCircuiting() {
        StringMatcher alwaysFalse = (observed, expected) -> false;
        StringMatcher alwaysTrue = (observed, expected) -> true;
        StringMatcher failIfEvaluated = (observed, expected) -> {
            throw new AssertionError("second predicate should not have been evaluated");
        };

        assertThat(alwaysFalse.and(failIfEvaluated).test("observed", "expected"))
                .isFalse();
        assertThat(alwaysTrue.or(failIfEvaluated).test("observed", "expected"))
                .isTrue();
    }

    @Test
    void matcherNamesDescribeMatchers() {
        assertThat(StringMatchers.strict()).hasToString("StrictStringMatcher");
        assertThat(StringMatchers.regex()).hasToString("RegexStringMatcher");
        assertThat(StringMatchers.caseInsensitive()).hasToString("CaseInsensitiveStringMatcher");
        assertThat(StringMatchers.substring()).hasToString("SubstringMatcher");
        assertThat(StringMatchers.caseInsensitiveSubstring()).hasToString("CaseInsensitiveSubstringMatcher");
        assertThat(StringMatchers.always()).hasToString("AlwaysStringMatcher");
        assertThat(StringMatchers.startsWith()).hasToString("StartsWithStringMatcher");
    }
}
