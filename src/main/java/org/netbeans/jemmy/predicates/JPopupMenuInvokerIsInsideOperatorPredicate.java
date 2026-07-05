package org.netbeans.jemmy.predicates;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

import javax.swing.*;
import java.awt.*;

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
