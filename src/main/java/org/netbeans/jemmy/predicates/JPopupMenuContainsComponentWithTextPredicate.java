package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;


public class JPopupMenuContainsComponentWithTextPredicate implements Predicate<Component> {
    private final Predicate<Component> predicate;

    public JPopupMenuContainsComponentWithTextPredicate(String menuItemText, StringComparator stringComparator) {
        predicate = new JMenuItemByTextPredicate(menuItemText, stringComparator);
    }

    @Override
    public boolean test(Component component) {
        return (component instanceof JPopupMenu)
               && (new ComponentSearcher((Container) component).findComponent(predicate) != null);
    }
}
