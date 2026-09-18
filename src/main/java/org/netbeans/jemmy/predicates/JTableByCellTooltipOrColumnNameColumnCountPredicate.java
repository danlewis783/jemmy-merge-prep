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
package org.netbeans.jemmy.predicates;

import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import java.awt.Component;
import java.util.Objects;
import java.util.function.Predicate;

public class JTableByCellTooltipOrColumnNameColumnCountPredicate extends JTableByCellTooltipPredicate {

    private final String columnName;
    private final int columnCount;
    private final String tabbedPaneName;

    public JTableByCellTooltipOrColumnNameColumnCountPredicate(String tooltip, int row,
                                                               int column, String columnName, int columnCount, String tabbedPaneName) {
        super(tooltip, row, column);
        this.columnName = columnName;
        this.columnCount = columnCount;
        this.tabbedPaneName = tabbedPaneName;
    }

    @Override
    public boolean isMatchingTable(JTable table, int r, int c, String tooltip) {
        if (tabbedPaneName != null && !tabbedPaneName.equals("*default*")) {
            // single non-blocking lookup: predicates are evaluated on the EDT, where waiting is
            // forbidden; the enclosing wait loop re-evaluates this predicate on every poll anyway
            JTabbedPane tabbedPane = JTabbedPaneOperator.findAncestorJTabbedPane(
                    table, newTabbedPaneChooserByTabName(tabbedPaneName));
            if (tabbedPane == null) {
                return false;
            }
        }

        if (table.getColumnCount() != columnCount) {
            return false;
        }

        // Check for embedded unicode in the column name (i.e. superscripts in the units). Do not
        // use the column name to match the table if it contains unicode - use the tooltip instead.
        String tableColumnName = table.getColumnName(c);
        if (columnName != null && isPureAscii(columnName) && isPureAscii(tableColumnName)
                && !columnNameEquals(tableColumnName, columnName)) {
            return false;
        }

        return tooltip == null || super.isMatchingTable(table, r, c, tooltip);
    }

    private boolean columnNameEquals(String tableColumnName, String expectedColumnName) {
        // Consider "\n\nSta\n " to be equal to "Sta"
        return getComparator().equals(strip(tableColumnName), strip(expectedColumnName));
    }

    private static Predicate<Component> newTabbedPaneChooserByTabName(String tabName) {
        return new TabbedPaneByTitlePredicate(tabName, StringComparators.caseInsensitive());
    }

    private static boolean isPureAscii(String value) {
        Objects.requireNonNull(value, "value");

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) > 0x7F) {
                return false;
            }
        }

        return true;
    }

    private static String strip(String value) {
        Objects.requireNonNull(value, "value");

        int start = 0;
        int end = value.length();

        while (start < end && Character.isWhitespace(value.charAt(start))) {
            start++;
        }

        while (start < end && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }

        return value.substring(start, end);
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + ", columnName=" + (columnName == null ? "null" : "\"" + columnName + "\"")
                + ", columnCount=" + columnCount + ", tabbedPaneName="
                + (tabbedPaneName == null ? "null" : "\"" + tabbedPaneName + "\"");
    }
}
