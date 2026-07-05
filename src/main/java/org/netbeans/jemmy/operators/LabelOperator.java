package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.LabelByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import java.awt.*;
import java.util.concurrent.Callable;


public class LabelOperator extends ComponentOperator {
    public LabelOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public LabelOperator(Label b) {
        super(b);
    }

    public LabelOperator(ContainerOperator cont, int index) {
        this((Label) waitComponent(cont, PredicatesJ.of(Label.class), index));
    }

    public LabelOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public LabelOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Label) cont.waitSubComponent(PredicatesJ.of(Label.class, chooser), index));
    }

    public LabelOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this((Label) waitComponent(cont, new LabelByLabelPredicate(text, stringComparator), 0));
    }
    
    public LabelOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Label) waitComponent(cont, new LabelByLabelPredicate(text, stringComparator), index));
    }

    public int getAlignment() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Label) getSource()).getAlignment()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Label) getSource()).getText()));
    }

    public void setAlignment(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Label) getSource()).setAlignment(i);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Label) getSource()).setText(string);

            return null;
        }));
    }

    public static Label findLabel(Container cont, Predicate<Component> chooser, int index) {
        return (Label) findComponent(cont, PredicatesJ.of(Label.class, chooser), index);
    }

    public static Label findLabel(Container cont, Predicate<Component> chooser) {
        return findLabel(cont, chooser, 0);
    }

    public static Label findLabel(Container cont, String text, StringComparator stringComparator, int index) {
        return findLabel(cont, new LabelByLabelPredicate(text, stringComparator), index);
    }

    public static Label findLabel(Container cont, String text, StringComparator stringComparator) {
        return findLabel(cont, text, stringComparator, 0);
    }

    public static Label waitLabel(Container cont, Predicate<Component> chooser, int index) {
        return (Label) waitComponent(cont, PredicatesJ.of(Label.class, chooser), index);
    }

    public static Label waitLabel(Container cont, Predicate<Component> chooser) {
        return waitLabel(cont, chooser, 0);
    }

    public static Label waitLabel(Container cont, String text, StringComparator stringComparator, int index) {
        return waitLabel(cont, new LabelByLabelPredicate(text, stringComparator), index);
    }

    public static Label waitLabel(Container cont, String text, StringComparator stringComparator) {
        return waitLabel(cont, text, stringComparator, 0);
    }

}
