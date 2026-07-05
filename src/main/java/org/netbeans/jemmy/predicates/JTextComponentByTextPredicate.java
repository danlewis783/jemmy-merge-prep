package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.text.JTextComponent;
import java.awt.*;

public final class JTextComponentByTextPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String expectedText;

    public JTextComponentByTextPredicate(String expectedText, StringComparator comparator) {
        this.expectedText = expectedText;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return comparator.equals(((JTextComponent) (comp)).getText(), expectedText);
    }
}
