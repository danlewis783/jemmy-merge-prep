package org.netbeans.jemmy.operators;

import java.util.function.Function;
import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.drivers.DriverManager;
import org.netbeans.jemmy.drivers.TextDriver;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.TextComponentByTextPredicate;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.TextListener;
import java.util.concurrent.Callable;


public class TextComponentOperator extends ComponentOperator {
    private static final Logger logger = LoggerFactory.getLogger(TextComponentOperator.class);
    private final TextDriver driver;

    public TextComponentOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public TextComponentOperator(TextComponent b) {
        super(b);
        driver = DriverManager.newInstance(JemmyProperties.getInstance()).getTextDriver(getClass());
    }

    public TextComponentOperator(ContainerOperator cont, int index) {
        this((TextComponent) waitComponent(cont, PredicatesJ.of(TextComponent.class), index));
    }

    public TextComponentOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public TextComponentOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public TextComponentOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((TextComponent) cont.waitSubComponent(PredicatesJ.of(TextComponent.class, chooser), index));
    }

    public TextComponentOperator(ContainerOperator cont, String text,  StringComparator stringComparator, int index) {
        this((TextComponent) waitComponent(cont, new TextComponentByTextPredicate(text, stringComparator), index));
    }

    public void changeCaretPosition(int position) {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.changeCaretPosition(TextComponentOperator.this, position);

            return null;
        }, null, TimeoutKey.TextComponentOperator_ChangeCaretPositionTimeout);
    }

    public void selectText(int startPosition, int finalPosition) {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) obj -> {
            driver.selectText(TextComponentOperator.this, startPosition, finalPosition);

            return null;
        }, null, TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public int getPositionByText(String text, int index) {
        String allText = getText();
        int position = 0;
        int ind = 0;
        while ((position = allText.indexOf(text, position)) >= 0) {
            if (ind == index) {
                return position;
            } else {
                ind++;
            }

            position = position + text.length();
        }

        return -1;
    }

    public int getPositionByText(String text) {
        return getPositionByText(text, 0);
    }

    public void clearText() {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.clearText(TextComponentOperator.this);

            return null;
        }, null, TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void typeText(String text, int caretPosition) {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.typeText(TextComponentOperator.this, text, caretPosition);

            return null;
        }, null, TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void typeText(String text) {
        typeText(text, getCaretPosition());
    }

    public void enterText(String text) {
        makeComponentVisible();
        produceTimeRestricted((Function<Void, Void>) v -> {
            driver.enterText(TextComponentOperator.this, text);

            return null;
        }, null, TimeoutKey.TextComponentOperator_TypeTextTimeout);
    }

    public void addTextListener(TextListener textListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).addTextListener(textListener);

            return null;
        }));
    }

    public int getCaretPosition() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getCaretPosition()));
    }

    public String getSelectedText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectedText()));
    }

    public int getSelectionEnd() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectionEnd()));
    }

    public int getSelectionStart() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getSelectionStart()));
    }

    public String getText() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).getText()));
    }

    public boolean isEditable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((TextComponent) getSource()).isEditable()));
    }

    public void removeTextListener(TextListener textListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).removeTextListener(textListener);

            return null;
        }));
    }

    public void select(int i, int i1) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).select(i, i1);

            return null;
        }));
    }

    public void selectAll() {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).selectAll();

            return null;
        }));
    }

    public void setCaretPosition(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setCaretPosition(i);

            return null;
        }));
    }

    public void setEditable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setEditable(b);

            return null;
        }));
    }

    public void setSelectionEnd(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setSelectionEnd(i);

            return null;
        }));
    }

    public void setSelectionStart(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setSelectionStart(i);

            return null;
        }));
    }

    public void setText(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((TextComponent) getSource()).setText(string);

            return null;
        }));
    }

    protected TextDriver getTextDriver() {
        return driver;
    }

    public static TextComponent findTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (TextComponent) findComponent(cont, PredicatesJ.of(TextComponent.class, chooser), index);
    }

    public static TextComponent findTextComponent(Container cont, Predicate<Component> chooser) {
        return findTextComponent(cont, chooser, 0);
    }

    public static TextComponent findTextComponent(Container cont, String text, StringComparator stringComparator, int index) {
        return findTextComponent(cont, new TextComponentByTextPredicate(text, stringComparator),
                                 index);
    }

    public static TextComponent findTextComponent(Container cont, String text, StringComparator stringComparator) {
        return findTextComponent(cont, text, stringComparator, 0);
    }

    public static TextComponent waitTextComponent(Container cont, Predicate<Component> chooser, int index) {
        return (TextComponent) waitComponent(cont, PredicatesJ.of(TextComponent.class, chooser), index);
    }

    public static TextComponent waitTextComponent(Container cont, Predicate<Component> chooser) {
        return waitTextComponent(cont, chooser, 0);
    }

    public static TextComponent waitTextComponent(Container cont, String text, StringComparator stringComparator, int index) {
        return waitTextComponent(cont, new TextComponentByTextPredicate(text, stringComparator),
                index);
    }

    public static TextComponent waitTextComponent(Container cont, String text, StringComparator stringComparator) {
        return waitTextComponent(cont, text, stringComparator, 0);
    }

}
