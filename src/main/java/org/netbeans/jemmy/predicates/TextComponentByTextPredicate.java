
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;

public final class TextComponentByTextPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public TextComponentByTextPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof TextComponent) && comparator.equals(((TextComponent) comp).getText(), label);
    }
}
