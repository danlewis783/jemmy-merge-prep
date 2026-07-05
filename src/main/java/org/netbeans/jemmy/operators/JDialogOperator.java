package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.FunctionRepeater;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.functions.DialogFunction;
import org.netbeans.jemmy.predicates.DialogShowingByTitlePredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.predicates.TopVisibleModalDialogPredicate;
import org.netbeans.jemmy.util.StringComparator;
import org.netbeans.jemmy.util.StringComparators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Callable;


public class JDialogOperator extends DialogOperator {
    private static final Logger logger = LoggerFactory.getLogger(JDialogOperator.class);

    public JDialogOperator() {
        this(0);
    }

    public JDialogOperator(int index) {
        this(waitJDialog(PredicatesJ.of(JDialog.class), index));
    }

    public JDialogOperator(JDialog w) {
        super(w);
    }

    public JDialogOperator(Predicate<Component> predicate) {
        this(predicate, 0);
    }

    public JDialogOperator(String title) {
        this(title, 0);
    }

    public JDialogOperator(WindowOperator owner) {
        this(owner, 0);
    }

    public JDialogOperator(Predicate<Component> predicate, int index) {
        this(waitJDialog(PredicatesJ.of(JDialog.class, predicate), index));
    }

    public JDialogOperator(String title, int index) {
        this(PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, StringComparators.strict())),
             index);
    }

    public JDialogOperator(WindowOperator owner, int index) {
        this(waitJDialog(owner, PredicatesJ.of(JDialog.class), index));
    }

    public JDialogOperator(WindowOperator owner, Predicate<Component> predicate) {
        this(owner, predicate, 0);
    }

    public JDialogOperator(WindowOperator owner, String title, StringComparator stringComparator) {
        this(owner, title, stringComparator, 0);
    }

    public JDialogOperator(WindowOperator owner, Predicate<Component> predicate, int index) {
        this((JDialog) owner.waitSubWindow(PredicatesJ.of(JDialog.class, predicate), index));
    }

    public JDialogOperator(WindowOperator owner, String title, StringComparator stringComparator, int index) {
        this(waitJDialog(owner,
                         PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)),
                         index));
    }

    public AccessibleContext getAccessibleContext() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> getSource().getAccessibleContext()));
    }

    public Container getContentPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getContentPane()));
    }

    public int getDefaultCloseOperation() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getDefaultCloseOperation()));
    }

    public Component getGlassPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getGlassPane()));
    }

    public JMenuBar getJMenuBar() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getJMenuBar()));
    }

    public JLayeredPane getLayeredPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getLayeredPane()));
    }

    public JRootPane getRootPane() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JDialog) getSource()).getRootPane()));
    }

    public void setContentPane(Container container) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setContentPane(container);

            return null;
        }));
    }

    public void setDefaultCloseOperation(int i) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setDefaultCloseOperation(i);

            return null;
        }));
    }

    public void setGlassPane(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setGlassPane(component);

            return null;
        }));
    }

    public void setJMenuBar(JMenuBar jMenuBar) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setJMenuBar(jMenuBar);

            return null;
        }));
    }

    public void setLayeredPane(JLayeredPane jLayeredPane) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setLayeredPane(jLayeredPane);

            return null;
        }));
    }

    public void setLocationRelativeTo(Component component) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JDialog) getSource()).setLocationRelativeTo(component);

            return null;
        }));
    }

    public static JDialog findJDialog(Predicate<Component> predicate, int index) {
        return (JDialog) DialogFunction.getDialog(PredicatesJ.of(JDialog.class, predicate), index);
    }

    public static JDialog findJDialog(Predicate<Component> predicate) {
        return findJDialog(predicate, 0);
    }

    public static JDialog findJDialog(String title, StringComparator stringComparator, int index) {
        return (JDialog) DialogFunction.getDialog(PredicatesJ.of(JDialog.class,
                new DialogShowingByTitlePredicate(title, stringComparator)), index);
    }

    public static JDialog findJDialog(String title, StringComparator stringComparator) {
        return findJDialog(title, stringComparator, 0);
    }

    public static JDialog findJDialog(Window owner, Predicate<Component> predicate, int index) {
        return (JDialog) DialogFunction.getDialog(owner, PredicatesJ.of(JDialog.class, predicate), index);
    }

    public static JDialog findJDialog(Window owner, Predicate<Component> predicate) {
        return findJDialog(owner, predicate, 0);
    }

    public static JDialog findJDialog(Window owner, String title, StringComparator stringComparator, int index) {
        return (JDialog) DialogFunction.getDialog(owner,
                PredicatesJ.of(JDialog.class, new DialogShowingByTitlePredicate(title, stringComparator)),
                index);
    }

    public static JDialog findJDialog(Window owner, String title, StringComparator stringComparator) {
        return findJDialog(owner, title, stringComparator, 0);
    }

    public static JDialog waitJDialog(Predicate<Component> predicate) {
        return waitJDialog(predicate, 0);
    }

    public static JDialog waitJDialog(String title, StringComparator stringComparator, int index) {
        return waitJDialog(PredicatesJ.of(JDialog.class,
                                          new DialogShowingByTitlePredicate(title,
                                              stringComparator)), index);
    }

    public static JDialog waitJDialog(String title, StringComparator stringComparator) {
        return waitJDialog(title, stringComparator, 0);
    }

    public static JDialog waitJDialog(Window owner, Predicate<Component> predicate) {
        return waitJDialog(owner, predicate, 0);
    }

    public static JDialog waitJDialog(Window owner, String title, StringComparator stringComparator, int index) {
        return waitJDialog(owner,
                           PredicatesJ.of(JDialog.class,
                                          new DialogShowingByTitlePredicate(title,
                                              stringComparator)), index);
    }

    public static JDialog waitJDialog(Window owner, String title, StringComparator stringComparator) {
        return waitJDialog(owner, title, stringComparator, 0);
    }

    public static Dialog getTopModalDialog() {
        return DialogFunction.getDialog(new TopVisibleModalDialogPredicate());
    }

    protected static JDialog waitJDialog(Predicate<Component> predicate, int index) {
        return waitJDialog((Window) null, predicate, index);
    }

    protected static JDialog waitJDialog(WindowOperator owner, Predicate<Component> predicate, int index) {
        return waitJDialog((Window) owner.getSource(), predicate, index);
    }

    protected static JDialog waitJDialog(Window owner, Predicate<Component> predicate, int index) {
        try {
            return (JDialog) FunctionRepeater.on(
                    new DialogFunction(index, owner,
                            PredicatesJ.of(JDialog.class, predicate)),
                    TimeoutKey.DialogWaiter_WaitDialogTimeout).runUntilNotNull(null);
        } catch (InterruptedException e) {
            logger.warn("", e);

            return null;
        }
    }
}
