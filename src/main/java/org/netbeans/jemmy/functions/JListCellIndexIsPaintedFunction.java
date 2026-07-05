
package org.netbeans.jemmy.functions;

import java.util.function.Function;
import java.util.function.Supplier;

import org.netbeans.jemmy.operators.JListOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;


public class JListCellIndexIsPaintedFunction implements Function<Integer, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(JListCellIndexIsPaintedFunction.class);
    private final Supplier<JList> fileListSupplier;

    public JListCellIndexIsPaintedFunction(Supplier<JList> fileListSupplier) {
        this.fileListSupplier = fileListSupplier;
    }

    @Override
    public Boolean apply(Integer cellIdx) {
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
