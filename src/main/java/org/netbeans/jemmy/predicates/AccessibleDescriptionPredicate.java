
package org.netbeans.jemmy.predicates;

import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

import javax.accessibility.AccessibleContext;

public final class AccessibleDescriptionPredicate extends AccessibilityPredicate {
    private final StringComparator comparator;
    private final String description;

    public AccessibleDescriptionPredicate(String description) {
        this(description, StringComparators.strict());
    }

    public AccessibleDescriptionPredicate(String description, StringComparator comparator) {
        this.description = description;
        this.comparator = comparator;
    }

    @Override
    public final boolean checkContext(AccessibleContext context) {
        return comparator.equals(context.getAccessibleDescription(), description);
    }
}
