
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;

public final class JSpinnerByTextPredicate implements Predicate<Component> {
    final StringComparator comparator;
    final String label;

    public JSpinnerByTextPredicate(String label, StringComparator comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    @Override
    public boolean test(Component comp) {
        return (comp instanceof JSpinner) && comparator.equals(((JSpinner) comp).getValue().toString(), label);
    }
}
