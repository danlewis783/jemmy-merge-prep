package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import static org.junit.jupiter.api.Assertions.assertEquals;
class jemmy_039 {
    private static final String[][] data = {
        { "one", ".n.", "true" }, { "one", "n", "false" }, { "one", ".e", "false" }, { "one", ".*e", "true" },
        { "one", "..*e", "true" }, { "one", "...*e", "true" }, { "one", "....*e", "false" }, { "teen", "te*.", "true" },
        { "seventeen", ".*e*.", "true" }, { "seventeen", "sevente*.", "true" }, { "seventeen", ".*ent.*", "true" }
    };




    @Test
    void test() {
        StringComparator comparator = StringComparators.regex();
        for (String[] arr : data) {
            assertEquals(comparator.equals(arr[0], arr[1]) ? "true" : "false", arr[2]);
        }
    }
}
