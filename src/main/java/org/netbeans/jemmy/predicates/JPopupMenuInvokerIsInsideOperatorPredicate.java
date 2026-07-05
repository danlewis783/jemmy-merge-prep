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
import java.awt.Container;
import java.util.function.Predicate;
import javax.swing.JPopupMenu;
import org.netbeans.jemmy.operators.ComponentOperator;

public final class JPopupMenuInvokerIsInsideOperatorPredicate implements Predicate<Component> {
    private final ComponentOperator componentOp;

    public JPopupMenuInvokerIsInsideOperatorPredicate(ComponentOperator componentOp) {
        this.componentOp = componentOp;
    }

    @Override
    public boolean test(Component component) {
        Component invoker = ((JPopupMenu) component).getInvoker();
        Component source = componentOp.getSource();

        return (invoker == source) || isInside(invoker, source) || isInside(source, invoker);
    }

    private boolean isInside(Component compA, Component compB) {
        return (compB instanceof Container) && ((Container) compB).isAncestorOf(compA);
    }
}
