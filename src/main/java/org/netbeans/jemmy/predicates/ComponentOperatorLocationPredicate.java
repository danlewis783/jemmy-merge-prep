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

import java.awt.Point;
import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class ComponentOperatorLocationPredicate<T extends ComponentOperator> implements Predicate<T> {
    private final Point minLocation;
    private final Point maxLocation;

    public ComponentOperatorLocationPredicate(Point exactLocation) {
        this(exactLocation, exactLocation);
    }

    public ComponentOperatorLocationPredicate(Point minLocation, Point maxLocation) {
        this.minLocation = new Point(minLocation);
        this.maxLocation = new Point(maxLocation);
    }

    @Override
    public boolean test(T compOp) {
        Point location = compOp.getLocation();

        return (location.x >= minLocation.x)
                && (location.x <= maxLocation.x)
                && (location.y >= minLocation.y)
                && (location.y <= maxLocation.y);
    }
}
