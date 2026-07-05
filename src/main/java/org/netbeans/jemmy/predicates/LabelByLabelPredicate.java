
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class LabelByLabelPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public LabelByLabelPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof Label) && comparator.equals(((Label) comp).getText(), label);
    }
}
