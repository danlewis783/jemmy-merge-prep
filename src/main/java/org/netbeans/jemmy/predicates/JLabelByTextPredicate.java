package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JLabelByTextPredicate implements Predicate<Component> {
    private final StringComparator comparator;
    private final String text;

    public JLabelByTextPredicate(String text, StringComparator comparator) {
        this.text = text;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof JLabel) && comparator.equals(((JLabel) comp).getText(), text);
    }
}
