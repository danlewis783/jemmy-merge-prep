
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class ChoiceBySelectedItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public ChoiceBySelectedItemPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof Choice) {
            if (((Choice) comp).getSelectedItem() != null) {
                return comparator.equals(((Choice) comp).getSelectedItem(), label);
            }
        }

        return false;
    }
}
