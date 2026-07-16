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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import javax.swing.JTable;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;

class TableValidatorTest {

    private final QueueTool queueTool = QueueTool.getInstance();

    private TableValidator validatorFor(Object[][] data, Object[] columnNames) {
        JTable table = queueTool.callOnQueue(() -> new JTable(data, columnNames));
        return new TableValidator(table);
    }

    private TableValidator sampleValidator() {
        return validatorFor(
                new Object[][] {
                    {1.5, 7, Boolean.TRUE, "alpha"},
                    {2.5, 8, Boolean.FALSE, "beta"},
                },
                new Object[] {"D", "I", "B", "S"});
    }

    @Test
    void validatesTypedCellsAndColumns() {
        TableValidator validator = sampleValidator();

        assertThatNoException().isThrownBy(() -> {
            validator.validateRowCount(2);
            validator.validateColumnCount(4);
            validator.validateCell(0, 0, 1.5, 1e-9);
            validator.validateCell(1, 1, 8);
            validator.validateCell(0, 2, true);
            validator.validateCell(1, 3, "beta");
            validator.validateColumn(0, 1e-9, 1.5, 2.5);
            validator.validateIntColumn(1, 7, 8);
            validator.validateColumn(2, true, false);
            validator.validateColumn(3, "alpha", "beta");
            validator.validateColumn("D", 1e-9, 1.5, 2.5);
            validator.validateIntColumn("I", 7, 8);
        });
    }

    @Test
    void validatesConstantColumn() {
        TableValidator validator = validatorFor(new Object[][] {{42, "same"}, {42, "same"}}, new Object[] {"N", "T"});

        assertThatNoException().isThrownBy(() -> {
            validator.validateConstantColumn(0, 42);
            validator.validateConstantColumn("T", "same");
        });
    }

    /** A null cell must produce an assertion failure naming the cell, not a bare NPE. */
    @Test
    void nullCellFailsWithCellDescription() {
        TableValidator validator = validatorFor(new Object[][] {{null}}, new Object[] {"A"});

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> validator.validateCell(0, 0, 1.0, 1e-9))
                .withMessageContaining("Wrong value for row 0, col 0");
    }

    @Test
    void nullCellMatchesNullStringExpectation() {
        TableValidator validator = validatorFor(new Object[][] {{null}}, new Object[] {"A"});

        assertThatNoException().isThrownBy(() -> validator.validateCell(0, 0, (String) null));
    }

    /** A non-numeric cell must produce an assertion failure naming the cell, not a bare NFE. */
    @Test
    void nonNumericCellFailsWithCellDescription() {
        TableValidator validator = validatorFor(new Object[][] {{"abc"}}, new Object[] {"A"});

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> validator.validateCell(0, 0, 1.0, 1e-9))
                .withMessageContaining("Wrong value for row 0, col 0");
    }

    /** A constant-column check against an empty table must fail, not pass vacuously. */
    @Test
    void constantColumnOnEmptyTableFails() {
        TableValidator validator = validatorFor(new Object[][] {}, new Object[] {"A"});

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> validator.validateConstantColumn(0, 42))
                .withMessageContaining("row count");
    }

    @Test
    void wrongColumnLengthFails() {
        TableValidator validator = sampleValidator();

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> validator.validateIntColumn(1, 7))
                .withMessageContaining("row count");
    }

    @Test
    void unknownColumnNameFails() {
        TableValidator validator = sampleValidator();

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> validator.validateIntColumn("Missing", 7, 8))
                .withMessageContaining("No column named Missing");
    }
}
