package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;


public class JTextFieldOperator extends JTextComponentOperator {
    public JTextFieldOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JTextFieldOperator(JTextField b) {
        super(b);
    }

    public JTextFieldOperator(ContainerOperator cont, int index) {
        this((JTextField) waitComponent(cont, PredicatesJ.of(JTextField.class), index));
    }

    public JTextFieldOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JTextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JTextFieldOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTextField) cont.waitSubComponent(PredicatesJ.of(JTextField.class, chooser), index));
    }

    public JTextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JTextField) waitComponent(cont,
                                        PredicatesJ.of(JTextField.class,
                                            new JTextComponentByTextPredicate(text,
                                                stringComparator)), index));
    }

    @Override
    public void waitText(String text, int position) {
        super.waitText(removeNewLines(text), position);
    }

    @Override
    public void waitText(String text, StringComparator stringComparator) {
        super.waitText(removeNewLines(text), stringComparator);
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public int getColumns() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextField) getSource()).getColumns()));
    }

    public int getHorizontalAlignment() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextField) getSource()).getHorizontalAlignment()));
    }

    public BoundedRangeModel getHorizontalVisibility() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextField) getSource()).getHorizontalVisibility()));
    }

    public int getScrollOffset() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextField) getSource()).getScrollOffset()));
    }

    public void postActionEvent() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).postActionEvent();

            return null;
        }));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void setActionCommand(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).setActionCommand(string);

            return null;
        }));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).setColumns(i);

            return null;
        }));
    }

    public void setHorizontalAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).setHorizontalAlignment(i);

            return null;
        }));
    }

    public void setScrollOffset(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextField) getSource()).setScrollOffset(i);

            return null;
        }));
    }

    private String removeNewLines(String text) {
        StringBuilder buff = new StringBuilder(text);
        int i = 0;
        while (i < buff.length()) {
            if (buff.charAt(i) != '\n') {
                i++;
            } else {
                buff.deleteCharAt(i);
            }
        }

        return buff.toString();
    }

    public static JTextField findJTextField(Container cont, Predicate<Component> chooser, int index) {
        return (JTextField) findJTextComponent(cont, PredicatesJ.of(JTextField.class, chooser), index);
    }

    public static JTextField findJTextField(Container cont, Predicate<Component> chooser) {
        return findJTextField(cont, chooser, 0);
    }

    public static JTextField findJTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return findJTextField(cont,
                              PredicatesJ.of(JTextField.class,
                                  new JTextComponentByTextPredicate(text, stringComparator)), index);
    }

    public static JTextField findJTextField(Container cont, String text, StringComparator stringComparator) {
        return findJTextField(cont, text, stringComparator, 0);
    }

    public static JTextField waitJTextField(Container cont, Predicate<Component> chooser, int index) {
        return (JTextField) waitJTextComponent(cont, PredicatesJ.of(JTextField.class, chooser), index);
    }

    public static JTextField waitJTextField(Container cont, Predicate<Component> chooser) {
        return waitJTextField(cont, chooser, 0);
    }

    public static JTextField waitJTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJTextField(cont,
                              PredicatesJ.of(JTextField.class,
                                  new JTextComponentByTextPredicate(text, stringComparator)), index);
    }

    public static JTextField waitJTextField(Container cont, String text, StringComparator stringComparator) {
        return waitJTextField(cont, text, stringComparator, 0);
    }
}
