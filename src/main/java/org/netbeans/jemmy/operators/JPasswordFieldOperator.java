package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;


public class JPasswordFieldOperator extends JTextFieldOperator {
    public static final String ECHO_CHAR_DPROP = "Echo char";

    public JPasswordFieldOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JPasswordFieldOperator(JPasswordField b) {
        super(b);
    }

    public JPasswordFieldOperator(ContainerOperator cont, int index) {
        this((JPasswordField) waitComponent(cont, PredicatesJ.of(JPasswordField.class), index));
    }

    public JPasswordFieldOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JPasswordFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JPasswordFieldOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JPasswordField) cont.waitSubComponent(PredicatesJ.of(JPasswordField.class, chooser), index));
    }

    public JPasswordFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JPasswordField) waitComponent(cont,
                PredicatesJ.of(JPasswordField.class,
                               new JTextComponentByTextPredicate(text,
                                   stringComparator)), index));
    }

    public boolean echoCharIsSet() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPasswordField) getSource()).echoCharIsSet()));
    }

    public char getEchoChar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JPasswordField) getSource()).getEchoChar()));
    }

    public char[] getPassword() {
        return (char[]) QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Object>) () -> ((JPasswordField) getSource()).getPassword()));
    }

    public void setEchoChar(char c) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JPasswordField) getSource()).setEchoChar(c);

            return null;
        }));
    }

    public static JPasswordField findJPasswordField(Container cont, Predicate<Component> chooser, int index) {
        return (JPasswordField) findJTextComponent(cont, PredicatesJ.of(JPasswordField.class, chooser), index);
    }

    public static JPasswordField findJPasswordField(Container cont, Predicate<Component> chooser) {
        return findJPasswordField(cont, chooser, 0);
    }

    public static JPasswordField findJPasswordField(Container cont, String text, StringComparator stringComparator, int index) {
        return findJPasswordField(cont,
                                  PredicatesJ.of(JPasswordField.class,
                                      new JTextComponentByTextPredicate(text,
                                          stringComparator)), index);
    }

    public static JPasswordField findJPasswordField(Container cont, String text, StringComparator stringComparator) {
        return findJPasswordField(cont, text, stringComparator, 0);
    }

    public static JPasswordField waitJPasswordField(Container cont, Predicate<Component> chooser, int index) {
        return (JPasswordField) waitJTextComponent(cont, PredicatesJ.of(JPasswordField.class, chooser), index);
    }

    public static JPasswordField waitJPasswordField(Container cont, Predicate<Component> chooser) {
        return waitJPasswordField(cont, chooser, 0);
    }

    public static JPasswordField waitJPasswordField(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJPasswordField(cont,
                                  PredicatesJ.of(JPasswordField.class,
                                      new JTextComponentByTextPredicate(text,
                                          stringComparator)), index);
    }

    public static JPasswordField waitJPasswordField(Container cont, String text, StringComparator stringComparator) {
        return waitJPasswordField(cont, text, stringComparator, 0);
    }
}
