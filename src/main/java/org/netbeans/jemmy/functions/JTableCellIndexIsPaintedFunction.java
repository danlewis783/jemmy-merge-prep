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

package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.JTable;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.operators.JTableOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Table analogue of {@link JListCellIndexIsPaintedFunction}, for file choosers whose file list is a details-view
 * {@code JTable}.
 */
public class JTableCellIndexIsPaintedFunction implements Function<Integer, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(JTableCellIndexIsPaintedFunction.class);
    private final Supplier<JTable> fileTableSupplier;

    public JTableCellIndexIsPaintedFunction(Supplier<JTable> fileTableSupplier) {
        this.fileTableSupplier = fileTableSupplier;
    }

    @Override
    public @Nullable Boolean apply(Integer cellIdx) {
        JTable jTable = fileTableSupplier.get();
        JTableOperator jTableOperator = JTableOperator.of(jTable);
        int rowIdxLast = jTableOperator.getModel().getRowCount() - 1;
        if (rowIdxLast == -1) {
            return true;
        }

        int rowIdxCurrent = (cellIdx < 0) ? 0 : cellIdx;
        try {
            if (jTableOperator.getCellRect(rowIdxCurrent, 0, false) != null) {
                return jTableOperator.getCellRect(rowIdxLast, 0, false) != null;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            logger.warn("sometimes thrown from JTable.getCellRect when row exists but not painted", e);
            return null;
        }
    }
}
