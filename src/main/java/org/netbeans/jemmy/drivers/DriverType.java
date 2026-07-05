
package org.netbeans.jemmy.drivers;

public enum DriverType {
    Window("drivers.window"), Tree("drivers.tree"), Text("drivers.text"), Table("drivers.table"),
    Scroll("drivers.scroll"), OrderedList("drivers.orderedlist"), MultiSelList("drivers.multisellist"),
    Mouse("drivers.mouse"), Menu("drivers.menu"), List("drivers.list"), Key("drivers.key"),
    InternalFrame("drivers.internal_frame"), Frame("drivers.frame"), Focus("drivers.focus"), Button("drivers.button");

    private final String propKey;

    private DriverType(String propKey) {
        this.propKey = propKey;
    }

    public String getPropKey() {
        return propKey;
    }
}
