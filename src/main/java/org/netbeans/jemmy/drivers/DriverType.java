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

package org.netbeans.jemmy.drivers;

public enum DriverType {
    Window("drivers.window"),
    Tree("drivers.tree"),
    Text("drivers.text"),
    Table("drivers.table"),
    Scroll("drivers.scroll"),
    OrderedList("drivers.orderedlist"),
    MultiSelList("drivers.multisellist"),
    Mouse("drivers.mouse"),
    Menu("drivers.menu"),
    List("drivers.list"),
    Key("drivers.key"),
    InternalFrame("drivers.internal_frame"),
    Frame("drivers.frame"),
    Focus("drivers.focus"),
    Button("drivers.button");

    private final String propKey;

    private DriverType(String propKey) {
        this.propKey = propKey;
    }

    public String getPropKey() {
        return propKey;
    }
}
