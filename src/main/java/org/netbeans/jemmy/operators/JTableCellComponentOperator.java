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
package org.netbeans.jemmy.operators;

import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTableByCellTooltipPredicate;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import javax.swing.JTable;
import java.util.Objects;

public class JTableCellComponentOperator extends ComponentOperator {
    private final JTable table;
    private final String tooltip;
    private final int row;
    private final int col;

    private JTableCellComponentOperator(JTable table, String tooltip, int row, int col) {
        super(table);
        this.table = table;
        this.tooltip = Objects.requireNonNull(tooltip, "tooltip");

        if (row < 0) {
            throw new IllegalArgumentException("row " + row + " must not be negative");
        }
        if (col < 0) {
            throw new IllegalArgumentException("col " + col + " must not be negative");
        }
        this.row = row;
        this.col = col;
    }

    public static @Nullable JTableCellComponentOperator findByTooltip(JTable table, String tooltip, StringComparator stringComparator) {
        Objects.requireNonNull(tooltip, "tooltip");
        int [] cellCoords = findRowAndColumnForTooltip(JTableOperator.of(table), tooltip, stringComparator);
        if (cellCoords == null) {
            return null;
        }
        JTableCellComponentOperator result =
                new JTableCellComponentOperator(table, tooltip, cellCoords[0], cellCoords[1]);
        return result;
    }

    private static int[] findRowAndColumnForTooltip(JTableOperator tableOp, String tooltip, StringComparator stringComparator) {
        return QueueTool.getInstance().callOnQueue(() -> {
            int rowCount = tableOp.getRowCount();
            int columnCount = tableOp.getColumnCount();
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    if (JTableByCellTooltipPredicate.isMatchingTable(tableOp, row, column, tooltip, stringComparator)) {
                        return new int [] {row, column};
                    }
                }
            }
            return null;
        });
    }

    @Override
    public void clickForPopup() {
        JTableOperator jTableOperator = JTableOperator.of(table);
        jTableOperator.clickOnCell(row, col);
        jTableOperator.callPopupOnCell(row, col);
    }
}
