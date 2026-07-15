package org.netbeans.jemmy.util.assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.List;
import java.util.Objects;
import javax.swing.JTable;
import org.netbeans.jemmy.operators.JTableOperator;

public final class TableValidator {
    private final JTableOperator tableOp;

    public TableValidator(JTable table) {
        this(JTableOperator.of(table));
    }

    public TableValidator(JTableOperator tableOperator) {
        this.tableOp = tableOperator;
    }

    public void validateCell(int row, int col, double expect, double tolerance) {
        assertThat(Double.parseDouble(tableOp.getValueAt(row, col).toString()))
                .as(cellDescription(row, col))
                .isCloseTo(expect, org.assertj.core.data.Offset.offset(tolerance));
    }

    public void validateCell(int row, int col, int expect) {
        assertThat(Integer.parseInt(tableOp.getValueAt(row, col).toString()))
                .as(cellDescription(row, col))
                .isEqualTo(expect);
    }

    public void validateCell(int row, int col, boolean expect) {
        assertThat(tableOp.getValueAt(row, col).toString())
                .as(cellDescription(row, col))
                .isEqualTo(Boolean.toString(expect));
    }

    public void validateCell(int row, int col, String expect) {
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
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isEqualTo(expect.length);

        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect[row], tolerance);
        }
    }

    public void validateColumn(String columnName, double tolerance, double... expect) {
        validateColumn(findColumnIndexByName(columnName), tolerance, expect);
    }

    public void validateConstantColumn(int col, double tolerance, double expect) {
        int rows = tableOp.getRowCount();
        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect, tolerance);
        }
    }

    public void validateConstantColumn(String columnName, double tolerance, double expect) {
        validateConstantColumn(findColumnIndexByName(columnName), tolerance, expect);
    }

    public void validateIntColumn(int col, int... expect) {
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isEqualTo(expect.length);

        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect[row]);
        }
    }

    public void validateIntColumn(String columnName, int... expect) {
        validateIntColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, int expect) {
        int rows = tableOp.getRowCount();
        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect);
        }
    }

    public void validateConstantColumn(String columnName, int expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateColumn(int col, boolean... expect) {
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isEqualTo(expect.length);

        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect[row]);
        }
    }

    public void validateColumn(String columnName, boolean... expect) {
        validateColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, boolean expect) {
        int rows = tableOp.getRowCount();
        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect);
        }
    }

    public void validateConstantColumn(String columnName, boolean expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateColumn(int col, String... expect) {
        int rows = tableOp.getRowCount();
        assertThat(rows).as("row count").isEqualTo(expect.length);

        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect[row]);
        }
    }

    public void validateColumn(String columnName, String... expect) {
        validateColumn(findColumnIndexByName(columnName), expect);
    }

    public void validateConstantColumn(int col, String expect) {
        int rows = tableOp.getRowCount();
        for (int row = 0; row < rows; row++) {
            validateCell(row, col, expect);
        }
    }

    public void validateConstantColumn(String columnName, String expect) {
        validateConstantColumn(findColumnIndexByName(columnName), expect);
    }

    private int findColumnIndexByName(String columnName) {
        Objects.requireNonNull(columnName);
        int columnCount = tableOp.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (columnName.equals(tableOp.getColumnName(i))) {
                return i;
            }
        }

        fail("No column named " + columnName);
        return -1;
    }

    private static String cellDescription(int row, int col) {
        return String.format("Wrong value for row %d, col %d", row, col);
    }
}
