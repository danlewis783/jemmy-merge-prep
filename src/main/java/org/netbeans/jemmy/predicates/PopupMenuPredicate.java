
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import javax.swing.*;
import java.awt.*;

public final class PopupMenuPredicate implements Predicate<Component> {
    private final JMenu menu;

    public PopupMenuPredicate(JMenu menu) {
        this.menu = menu;
    }

    @Override
    public boolean test(Component comp) {
        return (comp == menu.getPopupMenu()) && comp.isShowing() && comp.isEnabled();
    }
}
