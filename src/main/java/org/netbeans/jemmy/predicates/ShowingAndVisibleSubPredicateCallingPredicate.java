package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;

import java.awt.*;

public final class ShowingAndVisibleSubPredicateCallingPredicate implements Predicate<Component> {
    private final Predicate<Component> subFinder;

    ShowingAndVisibleSubPredicateCallingPredicate(Predicate<Component> subFinder) {
        this.subFinder = subFinder;
    }

    @Override
    public boolean test(Component comp) {
        return comp.isShowing() && comp.isVisible() && subFinder.test(comp);
    }
}
