package org.netbeans.jemmy.predicates;

import java.awt.*;
import java.util.function.Predicate;
import javax.swing.*;
import org.netbeans.jemmy.util.StringComparator;

public class TrimmingNamePredicate implements Predicate<Component> {
    private final String name;
    private final StringComparator comparator;

    public TrimmingNamePredicate(String name, StringComparator comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {

        if (!(comp instanceof JComponent)) {
            return false;
        }

        JComponent jComponent = (JComponent) comp;
        String jComponentName = jComponent.getName();
        if (jComponentName == null) {
            return false;
        }

        String jComponentNameTrimmed = jComponentName.trim();
        boolean result = comparator.equals(jComponentNameTrimmed, name);
        return result;
    }
}
