package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.concurrent.Callable;


public class JTextPaneOperator extends JEditorPaneOperator {
    public JTextPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JTextPaneOperator(JTextPane b) {
        super(b);
    }

    public JTextPaneOperator(ContainerOperator cont, int index) {
        this((JTextPane) waitComponent(cont, PredicatesJ.of(JTextPane.class), index));
    }

    public JTextPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JTextPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JTextPane) cont.waitSubComponent(PredicatesJ.of(JTextPane.class, chooser), index));
    }

    public JTextPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JTextPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JTextPane) waitComponent(cont,
                                       PredicatesJ.of(JTextPane.class,
                                           new JTextComponentByTextPredicate(text, stringComparator)), index));
    }

    public Style addStyle(String string, Style style) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).addStyle(string, style)));
    }

    public AttributeSet getCharacterAttributes() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getCharacterAttributes()));
    }

    public MutableAttributeSet getInputAttributes() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getInputAttributes()));
    }

    public Style getLogicalStyle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getLogicalStyle()));
    }

    public AttributeSet getParagraphAttributes() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getParagraphAttributes()));
    }

    public Style getStyle(String string) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getStyle(string)));
    }

    public StyledDocument getStyledDocument() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JTextPane) getSource()).getStyledDocument()));
    }

    public void insertComponent(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).insertComponent(component);

            return null;
        }));
    }

    public void insertIcon(Icon icon) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).insertIcon(icon);

            return null;
        }));
    }

    public void removeStyle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).removeStyle(string);

            return null;
        }));
    }

    public void setCharacterAttributes(AttributeSet attributeSet, boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).setCharacterAttributes(attributeSet, b);

            return null;
        }));
    }

    public void setLogicalStyle(Style style) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).setLogicalStyle(style);

            return null;
        }));
    }

    public void setParagraphAttributes(AttributeSet attributeSet, boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).setParagraphAttributes(attributeSet, b);

            return null;
        }));
    }

    public void setStyledDocument(StyledDocument styledDocument) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JTextPane) getSource()).setStyledDocument(styledDocument);

            return null;
        }));
    }

    public static JTextPane findJTextPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTextPane) findJTextComponent(cont, PredicatesJ.of(JTextPane.class, chooser), index);
    }

    public static JTextPane findJTextPane(Container cont, Predicate<Component> chooser) {
        return findJTextPane(cont, chooser, 0);
    }

    public static JTextPane findJTextPane(Container cont, String text, StringComparator stringComparator, int index) {
        return findJTextPane(cont,
                             PredicatesJ.of(JTextPane.class,
                                 new JTextComponentByTextPredicate(text, stringComparator)), index);
    }

    public static JTextPane findJTextPane(Container cont, String text, StringComparator stringComparator) {
        return findJTextPane(cont, text, stringComparator, 0);
    }

    public static JTextPane waitJTextPane(Container cont, Predicate<Component> chooser, int index) {
        return (JTextPane) waitJTextComponent(cont, PredicatesJ.of(JTextPane.class, chooser), index);
    }

    public static JTextPane waitJTextPane(Container cont, Predicate<Component> chooser) {
        return waitJTextPane(cont, chooser, 0);
    }

    public static JTextPane waitJTextPane(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJTextPane(cont,
                             PredicatesJ.of(JTextPane.class,
                                 new JTextComponentByTextPredicate(text, stringComparator)), index);
    }

    public static JTextPane waitJTextPane(Container cont, String text, StringComparator stringComparator) {
        return waitJTextPane(cont, text, stringComparator, 0);
    }
}
