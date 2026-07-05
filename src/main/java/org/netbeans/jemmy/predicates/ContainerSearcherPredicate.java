package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.ComponentSearcher;

import java.awt.*;

public final class ContainerSearcherPredicate implements Predicate<Component> {
    private final Predicate<Component> subPredicate;

    public ContainerSearcherPredicate(Predicate<Component> subPredicate) {
        this.subPredicate = subPredicate;
    }

    @Override
    public boolean test(Component comp) {
        return comp instanceof Container && new ComponentSearcher((Container) comp).findComponent(subPredicate) != null;
    }
}
