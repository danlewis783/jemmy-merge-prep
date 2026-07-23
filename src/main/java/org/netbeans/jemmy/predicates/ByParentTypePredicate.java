package org.netbeans.jemmy.predicates;

import java.awt.Component;
import java.awt.Container;
import java.util.function.Predicate;

public final class ByParentTypePredicate<T extends Component> implements Predicate<Component> {
    private final Class<T> targetType;

    private ByParentTypePredicate(Class<T> type) {
        targetType = type;
    }

    public static <T extends Component> ByParentTypePredicate<T> create(Class<T> type) {
        return new ByParentTypePredicate<>(type);
    }

    @Override
    public boolean test(Component comp) {
        final Container parent = comp.getParent();
        return parent.getClass() == targetType;
    }
}
