
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JMenuItemByTextPredicate implements Predicate<Component> {
    private final String text;
    private final StringComparator stringComparator;

    public JMenuItemByTextPredicate(String text, StringComparator stringComparator) {
        this.text = text;
        this.stringComparator = stringComparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof JMenuItem) && stringComparator.equals(((JMenuItem) comp).getText(), text);
    }
}
