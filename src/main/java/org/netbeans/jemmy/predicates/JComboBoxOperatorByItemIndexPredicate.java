
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JComboBoxOperator;

public final class JComboBoxOperatorByItemIndexPredicate implements Predicate<JComboBoxOperator> {
    private final int itemIndex;

    public JComboBoxOperatorByItemIndexPredicate(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    @Override
    public boolean test(JComboBoxOperator jComboBoxOperator) {
        return jComboBoxOperator.getModel().getSize() > itemIndex;
    }
}
