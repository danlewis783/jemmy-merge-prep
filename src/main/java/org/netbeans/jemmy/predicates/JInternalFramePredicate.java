
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import javax.swing.*;
import java.awt.*;

public final class JInternalFramePredicate implements Predicate<Component> {
    private final Predicate<Component> predicate;

    public JInternalFramePredicate() {
        this(PredicatesJ.alwaysTrue());
    }

    public JInternalFramePredicate(Predicate<Component> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Component comp) {
        return ((comp instanceof JInternalFrame) || (comp instanceof JInternalFrame.JDesktopIcon))
               && predicate.test(comp);
    }
}
