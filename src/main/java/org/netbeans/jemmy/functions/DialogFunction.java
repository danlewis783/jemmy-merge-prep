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

package org.netbeans.jemmy.functions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.DialogSubPredicate;

public final class DialogFunction extends WindowFunction<Dialog> {
    public DialogFunction(int index, Window owner, Predicate<Component> predicate) {
        super(index, owner, predicate);
    }

    public static Dialog getDialog(Predicate<Component> predicate) {
        return (Dialog) WindowFunction.getWindow(null, new DialogSubPredicate(predicate), 0);
    }

    public static Dialog getDialog(Predicate<Component> predicate, int index) {
        return (Dialog) WindowFunction.getWindow(null, new DialogSubPredicate(predicate), index);
    }

    public static Dialog getDialog(Window owner, Predicate<Component> predicate, int index) {
        return (Dialog) WindowFunction.getWindow(owner, new DialogSubPredicate(predicate), index);
    }
}
