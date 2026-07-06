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
import javax.swing.JList;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.operators.JListOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JListCellIndexIsPaintedFunction implements Function<Integer, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(JListCellIndexIsPaintedFunction.class);
    private final Supplier<JList> fileListSupplier;

    public JListCellIndexIsPaintedFunction(Supplier<JList> fileListSupplier) {
        this.fileListSupplier = fileListSupplier;
    }

    @Override
    public @Nullable Boolean apply(Integer cellIdx) {
        JList jList = fileListSupplier.get();
        JListOperator jListOperator = new JListOperator(jList);
        int cellIdxLast = jListOperator.getModel().getSize() - 1;
        if (cellIdxLast == -1) {
            return true;
        }

        int cellIdxCurrent = (cellIdx < 0) ? 0 : cellIdx;
        try {
            if (jListOperator.getCellBounds(cellIdxCurrent, cellIdxCurrent) != null) {
                return jListOperator.getCellBounds(cellIdxLast, cellIdxLast) != null;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            logger.warn("sometimes thrown from JList.getCellBounds when item exists but not painted", e);
            return null;
        }
    }
}
