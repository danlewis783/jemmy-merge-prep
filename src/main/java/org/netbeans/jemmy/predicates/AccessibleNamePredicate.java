
package org.netbeans.jemmy.predicates;

import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import javax.accessibility.AccessibleContext;

public final class AccessibleNamePredicate extends AccessibilityPredicate {
    private final StringComparator comparator;
    private final String name;

    public AccessibleNamePredicate(String name) {
        this(name, StringComparators.strict());
    }

    public AccessibleNamePredicate(String name, StringComparator comparator) {
        this.name = name;
        this.comparator = comparator;
    }

    @Override
    public final boolean checkContext(AccessibleContext context) {
        return comparator.equals(context.getAccessibleName(), name);
    }
}
