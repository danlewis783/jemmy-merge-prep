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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultPathParserTest {
    private PathParser parser;

    @BeforeEach
    void beforeEach() {
        parser = new DefaultPathParser("|");
    }

    @Test
    void case1() {
        assertArrayEquals(new String[] {"a", "b", "c"}, parser.parse("a|b|c"));
    }

    @Test
    void case2() {
        assertArrayEquals(new String[] {"", "b", "c"}, parser.parse("|b|c"));
    }

    @Test
    void case3() {
        assertArrayEquals(new String[] {"a", "b", ""}, parser.parse("a|b|"));
    }

    @Test
    void case4() {
        assertArrayEquals(new String[] {"a"}, parser.parse("a"));
    }

    @Test
    void case5() {
        assertArrayEquals(new String[] {"", ""}, parser.parse("|"));
    }

    @Test
    void case6() {
        assertArrayEquals(new String[] {"", "", ""}, parser.parse("||"));
    }

    @Test
    void case7() {
        assertArrayEquals(new String[] {}, parser.parse(""));
    }
}
