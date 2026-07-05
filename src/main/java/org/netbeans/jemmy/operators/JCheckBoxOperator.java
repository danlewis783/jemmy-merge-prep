package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;


public class JCheckBoxOperator extends JToggleButtonOperator {
    public JCheckBoxOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JCheckBoxOperator(JCheckBox b) {
        super(b);
    }

    public JCheckBoxOperator(ContainerOperator cont, int index) {
        this((JCheckBox) waitComponent(cont, PredicatesJ.of(JCheckBox.class), index));
    }

    public JCheckBoxOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JCheckBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JCheckBoxOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JCheckBox) cont.waitSubComponent(PredicatesJ.of(JCheckBox.class, chooser), index));
    }

    public JCheckBoxOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JCheckBox) waitComponent(cont,
                                       PredicatesJ.of(JCheckBox.class,
                                           new AbstractButtonByTextPredicate(text, stringComparator)), index));
    }

    public static JCheckBox findJCheckBox(Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBox) findJToggleButton(cont, PredicatesJ.of(JCheckBox.class, chooser), index);
    }

    public static JCheckBox findJCheckBox(Container cont, Predicate<Component> chooser) {
        return findJCheckBox(cont, chooser, 0);
    }

    public static JCheckBox findJCheckBox(Container cont, String text, StringComparator stringComparator, int index) {
        return findJCheckBox(cont,
                             PredicatesJ.of(JCheckBox.class,
                                 new AbstractButtonByTextPredicate(text,
                                     stringComparator)), index);
    }

    public static JCheckBox findJCheckBox(Container cont, String text, StringComparator stringComparator) {
        return findJCheckBox(cont, text, stringComparator, 0);
    }

    public static JCheckBox waitJCheckBox(Container cont, Predicate<Component> chooser, int index) {
        return (JCheckBox) waitJToggleButton(cont, PredicatesJ.of(JCheckBox.class, chooser), index);
    }

    public static JCheckBox waitJCheckBox(Container cont, Predicate<Component> chooser) {
        return waitJCheckBox(cont, chooser, 0);
    }

    public static JCheckBox waitJCheckBox(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJCheckBox(cont,
                             PredicatesJ.of(JCheckBox.class,
                                 new AbstractButtonByTextPredicate(text,
                                     stringComparator)), index);
    }

    public static JCheckBox waitJCheckBox(Container cont, String text, StringComparator stringComparator) {
        return waitJCheckBox(cont, text, stringComparator, 0);
    }
}
