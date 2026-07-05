
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class ListByItemPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final int itemIndex;
    private final String label;

    public ListByItemPredicate(String label, int itemIndex, StringComparator comparator) {
        this.label = label;
        this.itemIndex = itemIndex;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        if (comp instanceof List) {
            List list = (List) comp;
            if (label == null) {
                return true;
            }

            if (list.getItemCount() > itemIndex) {
                int ii = itemIndex;
                if (ii == -1) {
                    ii = list.getSelectedIndex();

                    if (ii == -1) {
                        return false;
                    }
                }

                return comparator.equals(list.getItem(ii), label);
            }
        }

        return false;
    }
}
