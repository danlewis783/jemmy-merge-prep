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

import org.jspecify.annotations.Nullable;

public final class StringPropertyPredicate extends PropertyPredicate {
    public StringPropertyPredicate(String[] propNames, String[] results) {
        this(propNames, null, null, results);
    }

    public StringPropertyPredicate(
            String[] propNames, Object @Nullable [][] params, Class @Nullable [][] classes, String[] results) {
        super(propNames, params, classes, results);
    }

    @Override
    boolean checkProperty(Object value, Object etalon) {
        return value.toString().equals(etalon.toString());
    }
}
