
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;

public abstract class AccessibilityPredicate implements Predicate<Component> {
    @Override
    public final boolean test(Component comp) {
        if (comp instanceof JComponent) {
            return checkContext(comp.getAccessibleContext());
        } else if (comp instanceof JDialog) {
            return checkContext(comp.getAccessibleContext());
        } else if (comp instanceof JFrame) {
            return checkContext(comp.getAccessibleContext());
        } else if (comp instanceof JWindow) {
            return checkContext(comp.getAccessibleContext());
        } else {
            return false;
        }
    }

    protected abstract boolean checkContext(AccessibleContext context);
}
