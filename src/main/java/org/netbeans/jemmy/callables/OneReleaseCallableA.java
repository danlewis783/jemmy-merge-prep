package org.netbeans.jemmy.callables;

import java.util.function.Predicate;
import org.netbeans.jemmy.operators.ComponentOperator;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class OneReleaseCallableA extends OneReleaseCallable {
    private final ComponentOperator componentOperator;

    public OneReleaseCallableA(List<Predicate<Component>> predicates, ComponentOperator componentOperator) {
        super(predicates, 0, false);
        this.componentOperator = componentOperator;
    }

    @Override
    public MenuElement call() {
        MenuElement menuElement = (MenuElement) componentOperator.getSource();
        process(menuElement);
        return menuElement;
    }

    @Override
    public MenuElement getMenuElement() {
        return null;
    }
}
