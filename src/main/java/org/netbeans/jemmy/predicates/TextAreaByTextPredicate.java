
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class TextAreaByTextPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public TextAreaByTextPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof TextArea) && comparator.equals(((TextArea) comp).getText(), label);
    }
}
