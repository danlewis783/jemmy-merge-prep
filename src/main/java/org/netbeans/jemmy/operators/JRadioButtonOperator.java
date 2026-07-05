package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;


public class JRadioButtonOperator extends JToggleButtonOperator {
    public JRadioButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JRadioButtonOperator(JRadioButton b) {
        super(b);
    }

    public JRadioButtonOperator(ContainerOperator cont, int index) {
        this((JRadioButton) waitComponent(cont, PredicatesJ.of(JRadioButton.class), index));
    }

    public JRadioButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JRadioButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JRadioButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JRadioButton) cont.waitSubComponent(PredicatesJ.of(JRadioButton.class, chooser), index));
    }

    public JRadioButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JRadioButton) waitComponent(cont,
                                          PredicatesJ.of(JRadioButton.class,
                                              new AbstractButtonByTextPredicate(text, stringComparator)), index));
    }

    public static JRadioButton findJRadioButton(Container cont, Predicate<Component> chooser, int index) {
        return (JRadioButton) findJToggleButton(cont, PredicatesJ.of(JRadioButton.class, chooser), index);
    }

    public static JRadioButton findJRadioButton(Container cont, Predicate<Component> chooser) {
        return findJRadioButton(cont, chooser, 0);
    }

    public static JRadioButton findJRadioButton(Container cont, String text, StringComparator stringComparator, int index) {
        return findJRadioButton(cont,
                                PredicatesJ.of(JRadioButton.class,
                                    new AbstractButtonByTextPredicate(text,
                                        stringComparator)), index);
    }

    public static JRadioButton findJRadioButton(Container cont, String text, StringComparator stringComparator) {
        return findJRadioButton(cont, text, stringComparator, 0);
    }

    public static JRadioButton waitJRadioButton(Container cont, Predicate<Component> chooser, int index) {
        return (JRadioButton) waitJToggleButton(cont, PredicatesJ.of(JRadioButton.class, chooser), index);
    }

    public static JRadioButton waitJRadioButton(Container cont, Predicate<Component> chooser) {
        return waitJRadioButton(cont, chooser, 0);
    }

    public static JRadioButton waitJRadioButton(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJRadioButton(cont,
                                PredicatesJ.of(JRadioButton.class,
                                    new AbstractButtonByTextPredicate(text,
                                        stringComparator)), index);
    }

    public static JRadioButton waitJRadioButton(Container cont, String text, StringComparator stringComparator) {
        return waitJRadioButton(cont, text, stringComparator, 0);
    }
}
