package org.netbeans.jemmy.predicates;

import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.SupplierRepeater;
import org.netbeans.jemmy.TimeoutExpiredException;
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
            JTabbedPane tabbedPane = waitForOptionalTabUnder(table, tabbedPaneName);

            // JTabbedPane tabbedPane =
            // JTabbedPaneOperator.findJTabbedPaneUnder(table,
            // newTabbedPaneChooserByTabName(tabbedPaneName));
            if (tabbedPane == null) {
                return false;
            }
        }

        if (table.getColumnCount() != columnCount) {
            return false;
        }

        // Check for embedded unicode in the column name (i.e. superscripts in the units). Do
        // not use the
        // column name to match the table if it contains unicode - use the tooltip instead. This
        // is being done
        // to make sure the FIT HTML files execute correctly.
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

    private static @Nullable JTabbedPane waitForOptionalTabUnder(Component component, String tabTitle) {
        Objects.requireNonNull(component, "c");
        Objects.requireNonNull(tabTitle, "name");

        try {
            return waitForTabUnder(component, tabTitle);
        } catch (TimeoutExpiredException ignored) {
            return null;
        }
    }

    private static JTabbedPane waitForTabUnder(Component component, String tabTitle) {
        Objects.requireNonNull(component, "c");
        Objects.requireNonNull(tabTitle, "name");

        return SupplierRepeater
                .on(() -> JTabbedPaneOperator.findJTabbedPaneUnder(
                        component,
                        newTabbedPaneChooserByTabName(tabTitle)))
                .runUntilNotNull();
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
}
