package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JListOperator;
import org.netbeans.jemmy.util.StringComparator;

public final class JListOperatorByItemPredicate<T extends JListOperator> implements Predicate<T> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String label;

    public JListOperatorByItemPredicate(String label, int itemIndex, StringComparator comparator) {
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(T jListOp) {
        if (label == null) {
            return true;
        }

        if ((jListOp).getModel().getSize() > itemIndex) {
            int ii = itemIndex;
            if (ii == -1) {
                ii = (jListOp).getSelectedIndex();

                if (ii == -1) {
                    return false;
                }
            }

            return comparator.equals((jListOp).getModel().getElementAt(ii).toString(), label);
        }

        return false;
    }
}
