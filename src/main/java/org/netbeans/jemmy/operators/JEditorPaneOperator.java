
package org.netbeans.jemmy.operators;

import java.util.function.Predicate;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparator;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.EditorKit;
import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;


public class JEditorPaneOperator extends JTextComponentOperator {
    public JEditorPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    public JEditorPaneOperator(JEditorPane b) {
        super(b);
    }

    public JEditorPaneOperator(ContainerOperator cont, int index) {
        this((JEditorPane) waitComponent(cont, PredicatesJ.of(JEditorPane.class), index));
    }

    public JEditorPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public JEditorPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public JEditorPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JEditorPane) cont.waitSubComponent(PredicatesJ.of(JEditorPane.class, chooser), index));
    }

    public JEditorPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JEditorPane) waitComponent(cont,
                                         PredicatesJ.of(JEditorPane.class,
                                             new JTextComponentByTextPredicate(text, stringComparator)), index));
    }

    public void addHyperlinkListener(HyperlinkListener hyperlinkListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).addHyperlinkListener(hyperlinkListener);
            return null;
        }));
    }

    public void fireHyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).fireHyperlinkUpdate(hyperlinkEvent);
            return null;
        }));
    }

    public String getContentType() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JEditorPane) getSource()).getContentType()));
    }

    public EditorKit getEditorKit() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JEditorPane) getSource()).getEditorKit()));
    }

    public EditorKit getEditorKitForContentType(String string) {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JEditorPane) getSource()).getEditorKitForContentType(string)));
    }

    public URL getPage() {
        return QueueTool.getInstance().invokeSmoothly(Caller.of(() -> ((JEditorPane) getSource()).getPage()));
    }

    public void read(InputStream inputStream, Object object) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).read(inputStream, object);
            return null;
        }));
    }

    public void removeHyperlinkListener(HyperlinkListener hyperlinkListener) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).removeHyperlinkListener(hyperlinkListener);
            return null;
        }));
    }

    public void setContentType(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).setContentType(string);
            return null;
        }));
    }

    public void setEditorKit(EditorKit editorKit) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).setEditorKit(editorKit);
            return null;
        }));
    }

    public void setEditorKitForContentType(String string, EditorKit editorKit) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).setEditorKitForContentType(string, editorKit);
            return null;
        }));
    }

    public void setPage(String string) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).setPage(string);
            return null;
        }));
    }

    public void setPage(URL uRL) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).setPage(uRL);
            return null;
        }));
    }

    public static JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) findJTextComponent(cont, PredicatesJ.of(JEditorPane.class, chooser), index);
    }

    public static JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser) {
        return findJEditorPane(cont, chooser, 0);
    }

    public static JEditorPane findJEditorPane(Container cont, String text, StringComparator stringComparator, int index) {
        return findJEditorPane(cont,
                               PredicatesJ.of(JEditorPane.class,
                                   new JTextComponentByTextPredicate(text,
                                       stringComparator)), index);
    }

    public static JEditorPane findJEditorPane(Container cont, String text, StringComparator stringComparator) {
        return findJEditorPane(cont, text, stringComparator, 0);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) waitJTextComponent(cont, PredicatesJ.of(JEditorPane.class, chooser), index);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser) {
        return waitJEditorPane(cont, chooser, 0);
    }

    public static JEditorPane waitJEditorPane(Container cont, String text, StringComparator stringComparator, int index) {
        return waitJEditorPane(cont,
                               PredicatesJ.of(JEditorPane.class,
                                   new JTextComponentByTextPredicate(text,
                                       stringComparator)), index);
    }

    public static JEditorPane waitJEditorPane(Container cont, String text, StringComparator stringComparator) {
        return waitJEditorPane(cont, text, stringComparator, 0);
    }
}
