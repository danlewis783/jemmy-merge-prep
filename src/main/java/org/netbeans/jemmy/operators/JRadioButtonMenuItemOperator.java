package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.predicates.JRadioButtonMenuItemByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;


public class JRadioButtonMenuItemOperator extends JMenuItemOperator {
    public JRadioButtonMenuItemOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JRadioButtonMenuItemOperator(JRadioButtonMenuItem item) {
        super(item);
    }

    public JRadioButtonMenuItemOperator(ContainerOperator cont, int index) {
        this((JRadioButtonMenuItem) waitComponent(cont, PredicatesJ.of(JRadioButtonMenuItem.class), index));
    }

    public JRadioButtonMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JRadioButtonMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JRadioButtonMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JRadioButtonMenuItem) cont.waitSubComponent(PredicatesJ.of(JRadioButtonMenuItem.class, chooser), index));
    }

    public JRadioButtonMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JRadioButtonMenuItem) waitComponent(cont,
                new JRadioButtonMenuItemByLabelPredicate(text, stringComparator), index));
    }

}
