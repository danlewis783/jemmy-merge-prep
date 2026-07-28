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
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class StringComparatorsTest {
    static Stream<StringComparator> comparators() {
        return Stream.of(
                StringComparators.strict(),
                StringComparators.regex(),
                StringComparators.caseInsensitive(),
                StringComparators.substring(),
                StringComparators.caseInsensitiveSubstring(),
                StringComparators.alwaysEqual(),
                StringComparators.startsWith(),
                StringComparators.trimming());
    }

    static Stream<Arguments> comparatorNames() {
        return Stream.of(
                Arguments.of(StringComparators.strict(), "StrictStringComparator"),
                Arguments.of(StringComparators.regex(), "RegexStringComparator"),
                Arguments.of(StringComparators.caseInsensitive(), "CaseInsensitiveStringComparator"),
                Arguments.of(StringComparators.substring(), "SubstringComparator"),
                Arguments.of(StringComparators.caseInsensitiveSubstring(), "CaseInsensitiveSubstringComparator"),
                Arguments.of(StringComparators.alwaysEqual(), "AlwaysEqualStringComparator"),
                Arguments.of(StringComparators.startsWith(), "StartsWithStringComparator"),
                Arguments.of(StringComparators.trimming(), "TrimmingStringComparator"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("comparators")
    void honorsLegacyNullWildcardContract(StringComparator comparator) {
        assertThat(comparator.equals(null, null)).isTrue();
        assertThat(comparator.equals("observed", null)).isTrue();
        assertThat(comparator.equals(null, "expected")).isFalse();
    }

    @Test
    void strictMatchesEqualStringsOnly() {
        StringComparator comparator = StringComparators.strict();

        assertThat(comparator.equals("Save", "Save")).isTrue();
        assertThat(comparator.equals("Save", "save")).isFalse();
    }

    @Test
    void caseInsensitiveMatchesIgnoringCase() {
        StringComparator comparator = StringComparators.caseInsensitive();

        assertThat(comparator.equals("Save", "sAvE")).isTrue();
        assertThat(comparator.equals("Save", "Saved")).isFalse();
    }

    @Test
    void substringMatchesContainedText() {
        StringComparator comparator = StringComparators.substring();

        assertThat(comparator.equals("Save As", "Save")).isTrue();
        assertThat(comparator.equals("Save As", "save")).isFalse();
        assertThat(comparator.equals("Save As", "Open")).isFalse();
    }

    @Test
    void caseInsensitiveSubstringMatchesContainedTextIgnoringCase() {
        StringComparator comparator = StringComparators.caseInsensitiveSubstring();

        assertThat(comparator.equals("Save As", "save")).isTrue();
        assertThat(comparator.equals("Save As", "OPEN")).isFalse();
    }

    @Test
    void caseInsensitiveSubstringIsLocaleIndependent() {
        Locale previousLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));

            assertThat(StringComparators.caseInsensitiveSubstring().equals("TITLE", "title")).isTrue();
        } finally {
            Locale.setDefault(previousLocale);
        }
    }

    // formerly scenario test jemmy_039
    @ParameterizedTest(name = "\"{0}\" ~ /{1}/ = {2}")
    @CsvSource({
        "one,.n.,true",
        "one,n,false",
        "one,.e,false",
        "one,.*e,true",
        "one,..*e,true",
        "one,...*e,true",
        "one,....*e,false",
        "teen,te*.,true",
        "seventeen,.*e*.,true",
        "seventeen,sevente*.,true",
        "seventeen,.*ent.*,true"
    })
    void regexMatchesEntireObservedString(String observed, String expected, boolean matches) {
        StringComparator comparator = StringComparators.regex();

        assertThat(comparator.equals(observed, expected)).isEqualTo(matches);
    }

    @Test
    void alwaysEqualMatchesEveryNonNullObservedString() {
        StringComparator comparator = StringComparators.alwaysEqual();

        assertThat(comparator.equals("anything", "different")).isTrue();
        assertThat(comparator.equals(null, "different")).isFalse();
    }

    @Test
    void startsWithMatchesPrefixes() {
        StringComparator comparator = StringComparators.startsWith();

        assertThat(comparator.equals("Save As", "Save")).isTrue();
        assertThat(comparator.equals("Save As", "As")).isFalse();
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("comparatorNames")
    void comparatorNamesDescribeComparators(StringComparator comparator, String expectedName) {
        assertThat(comparator).hasToString(expectedName);
    }
}
