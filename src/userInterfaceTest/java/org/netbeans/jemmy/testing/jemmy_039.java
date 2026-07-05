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
package org.netbeans.jemmy.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

class jemmy_039 {
    private static final String[][] data = {
        {"one", ".n.", "true"}, {"one", "n", "false"}, {"one", ".e", "false"}, {"one", ".*e", "true"},
        {"one", "..*e", "true"}, {"one", "...*e", "true"}, {"one", "....*e", "false"}, {"teen", "te*.", "true"},
        {"seventeen", ".*e*.", "true"}, {"seventeen", "sevente*.", "true"}, {"seventeen", ".*ent.*", "true"}
    };

    @Test
    void test() {
        StringComparator comparator = StringComparators.regex();
        for (String[] arr : data) {
            assertEquals(comparator.equals(arr[0], arr[1]) ? "true" : "false", arr[2]);
        }
    }
}
