
package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;


public class TextFieldByTextPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String label;

    public TextFieldByTextPredicate(String lb, StringComparator comparator) {
        label = lb;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof TextField) && comparator.equals(((TextField) comp).getText(), label);
    }
}
