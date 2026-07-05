package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;


public class JToggleButtonOperator extends AbstractButtonOperator {
    public JToggleButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JToggleButtonOperator(JToggleButton b) {
        super(b);
    }

    public JToggleButtonOperator(ContainerOperator cont, int index) {
        this((JToggleButton) waitComponent(cont, PredicatesJ.of(JToggleButton.class), index));
    }

    public JToggleButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JToggleButtonOperator(ContainerOperator cont, StringComparator stringComparator, String text) {
        this(cont, text, stringComparator, 0);
    }

    public JToggleButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JToggleButton) cont.waitSubComponent(PredicatesJ.of(JToggleButton.class, chooser), index));
    }

    public JToggleButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JToggleButton) waitComponent(cont,
                                           PredicatesJ.of(JToggleButton.class,
                                               new AbstractButtonByTextPredicate(text, stringComparator)), index));
    }

    protected void prepareToClick() {
        makeComponentVisible();
    }

    public static JToggleButton findJToggleButton(Container cont, Predicate<Component> chooser, int index) {
        return (JToggleButton) findAbstractButton(cont, PredicatesJ.of(JToggleButton.class, chooser), index);
    }

    public static JToggleButton findJToggleButton(Container cont, Predicate<Component> chooser) {
        return findJToggleButton(cont, chooser, 0);
    }

    public static JToggleButton findJToggleButton(Container cont, String text, StringComparator stringComparator, int index) {
        return findJToggleButton(cont,
                                 PredicatesJ.of(JToggleButton.class,
                                     new AbstractButtonByTextPredicate(text,
                                         stringComparator)), index);
    }

    public static JToggleButton findJToggleButton(Container cont, String text, StringComparator stringComparator) {
        return findJToggleButton(cont, text, stringComparator, 0);
    }

    public static JToggleButton waitJToggleButton(Container cont, Predicate<Component> chooser, int index) {
        return (JToggleButton) waitAbstractButton(cont, PredicatesJ.of(JToggleButton.class, chooser), index);
    }

    public static JToggleButton waitJToggleButton(Container cont, Predicate<Component> chooser) {
        return waitJToggleButton(cont, chooser, 0);
    }

    public static JToggleButton waitJToggleButton(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJToggleButton(cont,
                                 PredicatesJ.of(JToggleButton.class,
                                     new AbstractButtonByTextPredicate(text,
                                         stringComparator)), index);
    }

    public static JToggleButton waitJToggleButton(Container cont, String text, StringComparator stringComparator) {
        return waitJToggleButton(cont, text, stringComparator, 0);
    }
}
