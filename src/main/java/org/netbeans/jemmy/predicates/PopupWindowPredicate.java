
package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import java.awt.*;

public final class PopupWindowPredicate implements Predicate<Component> {
    private final Predicate<Component> predicate;

    public PopupWindowPredicate(Predicate<Component> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Component comp) {
        return new ComponentSearcher((Container) comp).findComponent(predicate) != null;
    }
}
