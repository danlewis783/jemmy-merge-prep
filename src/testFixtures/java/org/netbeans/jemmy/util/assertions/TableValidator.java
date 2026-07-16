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
package org.netbeans.jemmy.util.assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import javax.swing.JTable;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.util.StringComparators;

public final class TableValidator {
    private final JTableOperator tableOp;

    public TableValidator(JTable table) {
        this(JTableOperator.of(table));
    }

    public TableValidator(JTableOperator tableOperator) {
        this.tableOp = tableOperator;
    }

    public void validateCell(int row, int col, double expect, double tolerance) {
        assertThat(parseDouble(row, col))
                .as(cellDescription(row, col))
                .isCloseTo(expect, org.assertj.core.data.Offset.offset(tolerance));
    }

    public void validateCell(int row, int col, int expect) {
        assertThat(parseInt(row, col)).as(cellDescription(row, col)).isEqualTo(expect);
    }

    public void validateCell(int row, int col, boolean expect) {
        assertThat(nonNullCellValue(row, col).toString())
                .as(cellDescription(row, col))
                .isEqualTo(Boolean.toString(expect));
    }

    public void validateCell(int row, int col, @Nullable String expect) {
        Object value = tableOp.getValueAt(row, col);
        if (expect == null) {
            assertThat(value).as(cellDescription(row, col)).isNull();
        } else {
            assertThat(value).as(cellDescription(row, col)).hasToString(expect);
        }
    }

    public void validateCell(int row, int col, List<String> expect) {
        assertThat(tableOp.getValueAt(row, col)).as(cellDescription(row, col)).isEqualTo(expect);
    }

    public void validateRowCount(int expect) {
        assertThat(tableOp.getRowCount()).as("row count").isEqualTo(expect);
    }

    public void validateColumnCount(int expect) {
        assertThat(tableOp.getColumnCount()).as("column count").isEqualTo(expect);
    }

    public void validateColumn(int col, double tolerance, double... expect) {
        validateRows(expect.length, row -> validateCell(row, col, expect[row], tolerance));
    }

    public void validateColumn(String columnName, double tolerance, double... expect) {
        validateColumn(findColumnIndexByName(columnName), tolerance, expect);
    }

    public void validateConstantColumn(int col, double tolerance, double expect) {
        validateNonEmptyRows(row -> validateCell(row, col, expect, tolerance));
    }

    public void validateConstantColumn(String columnName, double tolerance, double expect) {
        validateConstantColumn(findColumnIndexByName(columnName), tolerance, expect);
    }

    public void validateIntColumn(int col, int... expect) {
        validateRows(expect.length, row -> validateCell(row, col, expect[row]));
    }

    public void validateIntColumn(String columnName, int... expect) {
        validateIntColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, int expect) {
        validateNonEmptyRows(row -> validateCell(row, col, expect));
    }

    public void validateConstantColumn(String columnName, int expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateColumn(int col, boolean... expect) {
        validateRows(expect.length, row -> validateCell(row, col, expect[row]));
    }

    public void validateColumn(String columnName, boolean... expect) {
        validateColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, boolean expect) {
        validateNonEmptyRows(row -> validateCell(row, col, expect));
    }

    public void validateConstantColumn(String columnName, boolean expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateColumn(int col, String... expect) {
        validateRows(expect.length, row -> validateCell(row, col, expect[row]));
    }

    public void validateColumn(String columnName, String... expect) {
        validateColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, @Nullable String expect) {
        validateNonEmptyRows(row -> validateCell(row, col, expect));
    }

    public void validateConstantColumn(String columnName, @Nullable String expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    private void validateRows(int expectedRowCount, IntConsumer rowAssertion) {
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isEqualTo(expectedRowCount);

        for (int row = 0; row < rows; row++) {
            rowAssertion.accept(row);
        }
    }

    /** A constant-column check on an empty table would pass vacuously; require at least one row. */
    private void validateNonEmptyRows(IntConsumer rowAssertion) {
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isPositive();

        for (int row = 0; row < rows; row++) {
            rowAssertion.accept(row);
        }
    }

    private double parseDouble(int row, int col) {
        String text = nonNullCellValue(row, col).toString();
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new AssertionError(cellDescription(row, col) + ": value <" + text + "> is not a double", e);
        }
    }

    private int parseInt(int row, int col) {
        String text = nonNullCellValue(row, col).toString();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new AssertionError(cellDescription(row, col) + ": value <" + text + "> is not an int", e);
        }
    }

    private Object nonNullCellValue(int row, int col) {
        Object value = tableOp.getValueAt(row, col);
        assertThat(value).as(cellDescription(row, col)).isNotNull();
        return Objects.requireNonNull(value);
    }

    private int findColumnIndexByName(String columnName) {
        Objects.requireNonNull(columnName);
        int index = tableOp.findColumn(columnName, StringComparators.strict());
        if (index == -1) {
            fail("No column named " + columnName);
        }

        return index;
    }

    private static String cellDescription(int row, int col) {
        return String.format("Wrong value for row %d, col %d", row, col);
    }
}
