package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.JListOperator;

import java.awt.*;

public final class ByRenderedComponentPredicateListItemChooser implements JListOperator.ListItemChooser {
    private final Predicate<Component> predicate;

    public ByRenderedComponentPredicateListItemChooser(Predicate<Component> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean checkItem(JListOperator jListOperator, int index) {
        return predicate.test(jListOperator.getRenderedComponent(index));
    }
}
