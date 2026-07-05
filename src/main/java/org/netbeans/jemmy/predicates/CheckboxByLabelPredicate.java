
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class CheckboxByLabelPredicate implements Predicate<Component> {
    final StringComparator comparator;
    final String label;

    public CheckboxByLabelPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof Checkbox) {
            if (((Checkbox) comp).getLabel() != null) {
                return comparator.equals(((Checkbox) comp).getLabel(), label);
            }
        }

        return false;
    }
}
