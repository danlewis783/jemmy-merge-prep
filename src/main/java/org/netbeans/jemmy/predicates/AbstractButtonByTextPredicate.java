package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class AbstractButtonByTextPredicate implements Predicate<Component> {
    private final String expectedText;
    private final StringComparator stringComparator;

    public AbstractButtonByTextPredicate(String expectedText, StringComparator stringComparator) {
        this.expectedText = expectedText;
        this.stringComparator = stringComparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof AbstractButton)
               && stringComparator.equals(((AbstractButton) comp).getText(), expectedText);
    }
}
