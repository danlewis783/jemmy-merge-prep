package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.drivers.ButtonDriver;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.predicates.CheckboxByLabelPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ItemListener;
import java.util.concurrent.Callable;


public class CheckboxOperator extends ComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(CheckboxOperator.class);
    private final ButtonDriver driver;

    public CheckboxOperator(Checkbox b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getButtonDriver(getClass());
    }

    public CheckboxOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public CheckboxOperator(ContainerOperator cont, int index) {
        this((Checkbox) waitComponent(cont, PredicatesJ.of(Checkbox.class), index));
    }

    public CheckboxOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public CheckboxOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public CheckboxOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((Checkbox) cont.waitSubComponent(PredicatesJ.of(Checkbox.class, chooser), index));
    }

    public CheckboxOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((Checkbox) waitComponent(cont, new CheckboxByLabelPredicate(text, stringComparator), index));
    }

    public void changeSelection(boolean newValue) {
        makeComponentVisible();

        if (getState() != newValue) {
            try {
                waitComponentEnabled();
            } catch (InterruptedException e) {
                throw new JemmyException("Interrupted!", e);
            }

            driver.push(this);

            if (getVerification()) {
                waitSelected(newValue);
            }
        }
    }

    public void changeSelectionNoBlock(boolean selected) {
        produceNoBlocking((Function<Boolean, Void>) value -> {
            changeSelection(value);

            return null;
        }, selected);
    }

    public void waitSelected(boolean selected) {
        waitState(new CheckboxOperatorStateSelectedPredicate(selected));
    }

    public void addItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Checkbox) getSource()).addItemListener(itemListener);

            return null;
        }));
    }

    public CheckboxGroup getCheckboxGroup() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Checkbox) getSource()).getCheckboxGroup()));
    }

    public String getLabel() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Checkbox) getSource()).getLabel()));
    }

    public boolean getState() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Checkbox) getSource()).getState()));
    }

    public void removeItemListener(ItemListener itemListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Checkbox) getSource()).removeItemListener(itemListener);

            return null;
        }));
    }

    public void setCheckboxGroup(CheckboxGroup grp) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Checkbox) getSource()).setCheckboxGroup(grp);

            return null;
        }));
    }

    public void setLabel(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Checkbox) getSource()).setLabel(string);

            return null;
        }));
    }

    public void setState(boolean state) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Checkbox) getSource()).setState(state);

            return null;
        }));
    }

    public static Checkbox findCheckbox(Container cont, Predicate<Component> chooser, int index) {
        return (Checkbox) findComponent(cont, PredicatesJ.of(Checkbox.class, chooser), index);
    }

    public static Checkbox findCheckbox(Container cont, Predicate<Component> chooser) {
        return findCheckbox(cont, chooser, 0);
    }

    public static Checkbox findCheckbox(Container cont, String text, StringComparator stringComparator, int index) {
        return findCheckbox(cont, new CheckboxByLabelPredicate(text, stringComparator), index);
    }

    public static Checkbox findCheckbox(Container cont, String text, StringComparator stringComparator) {
        return findCheckbox(cont, text, stringComparator, 0);
    }

    public static Checkbox waitCheckbox(Container cont, Predicate<Component> chooser, int index) {
        return (Checkbox) waitComponent(cont, PredicatesJ.of(Checkbox.class, chooser), index);
    }

    public static Checkbox waitCheckbox(Container cont, Predicate<Component> chooser) {
        return waitCheckbox(cont, chooser, 0);
    }

    public static Checkbox waitCheckbox(Container cont, String text, StringComparator stringComparator, int index) {
        return waitCheckbox(cont, new CheckboxByLabelPredicate(text, stringComparator), index);
    }

    public static Checkbox waitCheckbox(Container cont, String text, StringComparator stringComparator) {
        return waitCheckbox(cont, text, stringComparator, 0);
    }

    private static class CheckboxOperatorStateSelectedPredicate<T extends CheckboxOperator> implements Predicate<T> {
        private final boolean selected;

        public CheckboxOperatorStateSelectedPredicate(boolean selected) {
            this.selected = selected;
        }

        @Override
        public boolean test(T jCheckBoxOp) {
            return jCheckBoxOp.getState() == selected;
        }
    }
}
