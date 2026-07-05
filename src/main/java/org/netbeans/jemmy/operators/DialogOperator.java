package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.DialogFunction;
import org.netbeans.jemmy.predicates.DialogOperatorShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.Callable;


public class DialogOperator extends WindowOperator {
    public static final String IS_MODAL_DPROP = "Modal";
    public static final String IS_RESIZABLE_DPROP = "Resizable";
    public static final String TITLE_DPROP = "Title";
    private static final Logger logger = LoggerFactory.getLogger(DialogOperator.class);

    public DialogOperator() {
        this(0);
    }

    public DialogOperator(Dialog w) {
        super(w);
    }

    public DialogOperator(int index) {
        this(waitDialog(PredicatesJ.of(Dialog.class), index));
    }

    public DialogOperator(Predicate<Component> chooser) {
        this(chooser, 0);
    }

    public DialogOperator(String title) {
        this(title, 0);
    }

    public DialogOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public DialogOperator(Predicate<Component> chooser, int index) {
        this(waitDialog(PredicatesJ.of(Dialog.class, chooser), index));
    }

    public DialogOperator(String title, int index) {
        this(new DialogShowingByTitlePredicate(title), index);
    }

    public DialogOperator(WindowOperator owner, int index) {
        this(waitDialog(owner, PredicatesJ.of(Dialog.class), index));
    }

    public DialogOperator(WindowOperator owner, Predicate<Component> chooser) {
        this(owner, chooser, 0);
    }

    public DialogOperator(WindowOperator owner, String title, StringComparator stringComparator) {
        this(owner, title, stringComparator, 0);
    }

    public DialogOperator(WindowOperator owner, Predicate<Component> chooser, int index) {
        this((Dialog) owner.waitSubWindow(PredicatesJ.of(Dialog.class, chooser), index));
    }

    public DialogOperator(WindowOperator owner, String title, StringComparator stringComparator, int index) {
        this(waitDialog(owner, new DialogShowingByTitlePredicate(title, stringComparator), index));
    }

    public void waitTitle(String title, StringComparator stringComparator) {
        waitState(new DialogOperatorShowingByTitlePredicate(title, stringComparator));
    }

    public String getTitle() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Dialog) getSource()).getTitle()));
    }

    public boolean isModal() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Dialog) getSource()).isModal()));
    }

    public boolean isResizable() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((Dialog) getSource()).isResizable()));
    }

    public void setModal(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Dialog) getSource()).setModal(b);

            return null;
        }));
    }

    public void setResizable(boolean b) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Dialog) getSource()).setResizable(b);

            return null;
        }));
    }

    public void setTitle(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((Dialog) getSource()).setTitle(string);

            return null;
        }));
    }

    protected static Dialog waitDialog(Predicate<Component> chooser, int index) {
        try {
            return FunctionRepeater.on(
                    new DialogFunction(index, null, PredicatesJ.of(Dialog.class, chooser)),
                    TimeoutKey.DialogWaiter_WaitDialogTimeout).runUntilNotNull(null);
        } catch (InterruptedException e) {
            logger.warn("", e);

            return null;
        }
    }

    protected static Dialog waitDialog(WindowOperator owner, Predicate<Component> chooser, int index) {
        return waitDialog((Window) owner.getSource(), chooser, index);
    }

    protected static Dialog waitDialog(Window owner, Predicate<Component> chooser, int index) {
        try {
            return FunctionRepeater.on(
                    new DialogFunction(index, owner, PredicatesJ.of(Dialog.class, chooser)),
                    TimeoutKey.DialogWaiter_WaitDialogTimeout).runUntilNotNull(null);
        } catch (InterruptedException e) {
            logger.warn("", e);

            return null;
        }
    }
}
