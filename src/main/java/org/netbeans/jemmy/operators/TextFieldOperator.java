package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;


public class TextFieldOperator extends TextComponentOperator {
    public TextFieldOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public TextFieldOperator(TextField b) {
        super(b);
    }

    public TextFieldOperator(ContainerOperator cont, int index) {
        this((TextField) waitComponent(cont, PredicatesJ.of(TextField.class), index));
    }

    public TextFieldOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public TextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public TextFieldOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((TextField) cont.waitSubComponent(PredicatesJ.of(TextField.class, chooser), index));
    }

    public TextFieldOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((TextField) waitComponent(cont, new TextFieldByTextPredicate(text, stringComparator), index));
    }

    public void addActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).addActionListener(actionListener);

            return null;
        }));
    }

    public boolean echoCharIsSet() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).echoCharIsSet()));
    }

    public int getColumns() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getColumns()));
    }

    public char getEchoChar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getEchoChar()));
    }

    public Dimension getMinimumSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getMinimumSize(i)));
    }

    public Dimension getPreferredSize(int i) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextField) getSource()).getPreferredSize(i)));
    }

    public void removeActionListener(ActionListener actionListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).removeActionListener(actionListener);

            return null;
        }));
    }

    public void setColumns(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextField) getSource()).setColumns(i);

            return null;
        }));
    }

    public static TextField findTextField(Container cont, Predicate<Component> chooser, int index) {
        return (TextField) findComponent(cont, PredicatesJ.of(TextField.class, chooser), index);
    }

    public static TextField findTextField(Container cont, Predicate<Component> chooser) {
        return findTextField(cont, chooser, 0);
    }

    public static TextField findTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return findTextField(cont, new TextFieldByTextPredicate(text, stringComparator), index);
    }

    public static TextField findTextField(Container cont, String text, StringComparator stringComparator) {
        return findTextField(cont, text, stringComparator, 0);
    }

    public static TextField waitTextField(Container cont, Predicate<Component> chooser, int index) {
        return (TextField) waitComponent(cont, PredicatesJ.of(TextField.class, chooser), index);
    }

    public static TextField waitTextField(Container cont, Predicate<Component> chooser) {
        return waitTextField(cont, chooser, 0);
    }

    public static TextField waitTextField(Container cont, String text, StringComparator stringComparator, int index) {
        return waitTextField(cont, new TextFieldByTextPredicate(text, stringComparator), index);
    }

    public static TextField waitTextField(Container cont, String text, StringComparator stringComparator) {
        return waitTextField(cont, text, stringComparator, 0);
    }

}
