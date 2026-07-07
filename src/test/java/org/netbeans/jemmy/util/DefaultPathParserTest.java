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
        assertThat(parser.parse("a|b|c")).isEqualTo(new String[] {"a", "b", "c"});
    }

    @Test
    void case2() {
        assertThat(parser.parse("|b|c")).isEqualTo(new String[] {"", "b", "c"});
    }

    @Test
    void case3() {
        assertThat(parser.parse("a|b|")).isEqualTo(new String[] {"a", "b", ""});
    }

    @Test
    void case4() {
        assertThat(parser.parse("a")).isEqualTo(new String[] {"a"});
    }

    @Test
    void case5() {
        assertThat(parser.parse("|")).isEqualTo(new String[] {"", ""});
    }

    @Test
    void case6() {
        assertThat(parser.parse("||")).isEqualTo(new String[] {"", "", ""});
    }

    @Test
    void case7() {
        assertThat(parser.parse("")).isEqualTo(new String[] {});
    }
}
