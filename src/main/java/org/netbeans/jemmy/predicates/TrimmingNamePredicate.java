/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.netbeans.jemmy.predicates;

import java.awt.Component;
import java.util.Objects;
import java.util.function.Predicate;
import org.netbeans.jemmy.util.StringComparator;

/**
 * Matches any component whose {@link Component#getName() name}, trimmed of surrounding
 * whitespace, matches the expected name; like {@link ComponentPredicates#byName} plus the trim.
 */
public final class TrimmingNamePredicate implements Predicate<Component> {
    private final String name;
    private final StringComparator comparator;

    public TrimmingNamePredicate(String name, StringComparator comparator) {
        this.name = Objects.requireNonNull(name, "name");
        this.comparator = Objects.requireNonNull(comparator, "comparator");
    }

    @Override
    public boolean test(Component comp) {
        String componentName = comp.getName();
        if (componentName == null) {
            return false;
        }

        return comparator.equals(componentName.trim(), name);
    }
}
