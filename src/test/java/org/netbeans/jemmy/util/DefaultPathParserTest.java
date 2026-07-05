package org.netbeans.jemmy.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DefaultPathParserTest {
    private PathParser parser;

    @BeforeEach
    void beforeEach() {
        parser = new DefaultPathParser("|");
    }

    @Test
    void case1() {
        assertArrayEquals(new String[]{"a", "b", "c"}, parser.parse("a|b|c"));
    }

    @Test
    void case2() {
        assertArrayEquals(new String[]{"", "b", "c"}, parser.parse("|b|c"));
    }

    @Test
    void case3() {
        assertArrayEquals(new String[]{"a", "b", ""}, parser.parse("a|b|"));
    }

    @Test
    void case4() {
        assertArrayEquals(new String[]{"a"}, parser.parse("a"));
    }

    @Test
    void case5() {
        assertArrayEquals(new String[]{"", ""}, parser.parse("|"));
    }

    @Test
    void case6() {
        assertArrayEquals(new String[]{"", "", ""}, parser.parse("||"));
    }

    @Test
    void case7() {
        assertArrayEquals(new String[]{}, parser.parse(""));
    }
}
