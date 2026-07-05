package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JCheckBoxMenuItemByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;


public class JCheckBoxMenuItemOperator extends JMenuItemOperator {
    public JCheckBoxMenuItemOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JCheckBoxMenuItemOperator(JCheckBoxMenuItem item) {
        super(item);
    }

    public JCheckBoxMenuItemOperator(ContainerOperator cont, int index) {
        this((JCheckBoxMenuItem) waitComponent(cont, PredicatesJ.of(JCheckBoxMenuItem.class), index));
    }

    public JCheckBoxMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JCheckBoxMenuItemOperator(ContainerOperator cont, String text, StringComparator comparator) {
        this(cont, text, comparator, 0);
    }

    public JCheckBoxMenuItemOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JCheckBoxMenuItem) cont.waitSubComponent(PredicatesJ.of(JCheckBoxMenuItem.class, chooser), index));
    }

    public JCheckBoxMenuItemOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JCheckBoxMenuItem) waitComponent(cont, new JCheckBoxMenuItemByLabelPredicate(text, stringComparator),
                index));
    }

    public boolean getState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JCheckBoxMenuItem) getSource()).getState()));
    }

    public void setState(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JCheckBoxMenuItem) getSource()).setState(b);

            return null;
        }));
    }
}
