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

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;

public final class DialogOperatorShowingByTitlePredicate implements Predicate<DialogOperator> {
    private final StringComparator comparator;
    private final String title;

    public DialogOperatorShowingByTitlePredicate(String title) {
        this(title, StringComparators.strict());
    }

    public DialogOperatorShowingByTitlePredicate(String title, StringComparator comparator) {
        this.title = title;
        this.comparator = comparator;
    }

    @Override
    public boolean test(DialogOperator dialogOp) {
        return dialogOp.isShowing() && comparator.equals((dialogOp).getTitle(), title);
    }
}
