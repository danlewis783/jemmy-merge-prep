package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.AbstractButtonByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;


public class JButtonOperator extends AbstractButtonOperator {
    public static final String IS_DEFAULT_DPROP = "Default button";

    public JButtonOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JButtonOperator(JButton b) {
        super(b);
    }

    public JButtonOperator(ContainerOperator cont, int index) {
        this((JButton) waitComponent(cont, PredicatesJ.of(JButton.class), index));
    }

    public JButtonOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JButtonOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JButton) cont.waitSubComponent(PredicatesJ.of(JButton.class, chooser), index));
    }

    public JButtonOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JButton) waitComponent(cont,
                                     PredicatesJ.of(JButton.class,
                                         new AbstractButtonByTextPredicate(text, stringComparator)), index));
    }

    public boolean isDefaultButton() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JButton) getSource()).isDefaultButton()));
    }

    public boolean isDefaultCapable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JButton) getSource()).isDefaultCapable()));
    }

    public void setDefaultCapable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JButton) getSource()).setDefaultCapable(b);

            return null;
        }));
    }

    protected void prepareToClick() {
        makeComponentVisible();
    }

    public static JButton findJButton(Container cont, Predicate<Component> chooser, int index) {
        return (JButton) findAbstractButton(cont, PredicatesJ.of(JButton.class, chooser), index);
    }

    public static JButton findJButton(Container cont, Predicate<Component> chooser) {
        return findJButton(cont, chooser, 0);
    }

    public static JButton findJButton(Container cont, String text, StringComparator stringComparator, int index) {
        return findJButton(cont,
                           PredicatesJ.of(JButton.class,
                                          new AbstractButtonByTextPredicate(text,
                                              stringComparator)), index);
    }

    public static JButton findJButton(Container cont, String text, StringComparator stringComparator) {
        return findJButton(cont, text, stringComparator, 0);
    }

    public static JButton waitJButton(Container cont, Predicate<Component> chooser, int index) {
        return (JButton) waitAbstractButton(cont, PredicatesJ.of(JButton.class, chooser), index);
    }

    public static JButton waitJButton(Container cont, Predicate<Component> chooser) {
        return waitJButton(cont, chooser, 0);
    }

    public static JButton waitJButton(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJButton(cont,
                           PredicatesJ.of(JButton.class,
                                          new AbstractButtonByTextPredicate(text,
                                              stringComparator)), index);
    }

    public static JButton waitJButton(Container cont, String text, StringComparator stringComparator) {
        return waitJButton(cont, text, stringComparator, 0);
    }
}
