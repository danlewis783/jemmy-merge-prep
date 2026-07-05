package org.netbeans.jemmy.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringComparatorsTest {
    @Test
    void testExact() {
        nullContractTest(StringComparators.strict());
    }

    @Test
    void testRegex() {
        nullContractTest(StringComparators.regex());
    }

    @Test
    void testCaseInsensitive() {
        nullContractTest(StringComparators.caseInsensitive());
    }

    @Test
    void testSubstring() {
        nullContractTest(StringComparators.substring());
    }

    @Test
    void testCaseInsensitiveSubstring() {
        nullContractTest(StringComparators.caseInsensitiveSubstring());
    }

    @Test
    void testAlwaysEqual() {
        nullContractTest(StringComparators.alwaysEqual());
    }

    private void nullContractTest(StringComparator comparator) {
        assertFalse(comparator.equals(null, ""));
        assertTrue(comparator.equals("", null));
    }
}
