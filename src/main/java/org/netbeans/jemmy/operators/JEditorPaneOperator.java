/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.netbeans.jemmy.operators;

import java.awt.Component;
import java.awt.Container;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.jspecify.annotations.Nullable;
import org.netbeans.jemmy.Caller;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

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
        this((JEditorPane) waitComponent(
                cont,
                PredicatesJ.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * Scrolls to a named anchor reference, waits for the caret to arrive there, and clicks it. Ported from
     * openjdk/jemmy-v2 (CODETOOLS-7902156).
     *
     * @throws IllegalArgumentException if the document has no anchor named {@code reference}
     * @throws IllegalComponentStateException if the reference is outside the visible area and no enclosing scroll
     *     pane exists to scroll it into view
     */
    public void clickOnReference(String reference) {
        int expectedCaretPos = getCaretPositionOfReference(reference);
        Rectangle viewBounds = modelToView(expectedCaretPos);
        Point expectedCaretPosLoc = new Point(viewBounds.x, viewBounds.y);
        JScrollPane scroll = (JScrollPane) getContainer(PredicatesJ.of(JScrollPane.class));
        if (scroll != null) {
            JScrollPaneOperator scroller = new JScrollPaneOperator(scroll);
            scroller.setVisualizer(new EmptyVisualizer());
            scroller.scrollToComponentRectangle(
                    getSource(), viewBounds.x, viewBounds.y, viewBounds.width, viewBounds.height);
            setCaretPosition(expectedCaretPos);
        } else if (getVisibleRect().contains(expectedCaretPosLoc)) {
            scrollToReference(reference);
        } else {
            throw new IllegalComponentStateException(
                    "Component doesn't contain JScrollPane and Reference is out of visible area");
        }

        waitStateOnQueue(op -> expectedCaretPosLoc.equals(
                ((JEditorPane) op.getSource()).getCaret().getMagicCaretPosition()));
        waitCaretPosition(expectedCaretPos);
        clickMouse(viewBounds.x, viewBounds.y, 1);
    }

    private int getCaretPositionOfReference(String reference) {
        Integer pos = QueueTool.getInstance().invokeSmoothly(Caller.of(() -> {
            Document doc = ((JEditorPane) getSource()).getDocument();
            if (doc instanceof HTMLDocument) {
                for (HTMLDocument.Iterator iter = ((HTMLDocument) doc).getIterator(HTML.Tag.A);
                        iter.isValid();
                        iter.next()) {
                    String nameAttr = (String) iter.getAttributes().getAttribute(HTML.Attribute.NAME);
                    if (reference.equals(nameAttr)) {
                        return iter.getStartOffset();
                    }
                }
            }

            return -1;
        }));
        if (pos == -1) {
            throw new IllegalArgumentException(
                    "Reference " + reference + " doesn't exist in the document " + getDocument() + ".");
        }

        return pos;
    }

    public void scrollToReference(String reference) {
        QueueTool.getInstance().invokeSmoothly(Caller.of((Callable<Void>) () -> {
            ((JEditorPane) getSource()).scrollToReference(reference);

            return null;
        }));
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
        return QueueTool.getInstance()
                .invokeSmoothly(Caller.of(() -> ((JEditorPane) getSource()).getEditorKitForContentType(string)));
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

    public static @Nullable JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) findJTextComponent(cont, PredicatesJ.of(JEditorPane.class, chooser), index);
    }

    public static @Nullable JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser) {
        return findJEditorPane(cont, chooser, 0);
    }

    public static @Nullable JEditorPane findJEditorPane(
            Container cont, String text, StringComparator stringComparator, int index) {
        return findJEditorPane(
                cont,
                PredicatesJ.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JEditorPane findJEditorPane(
            Container cont, String text, StringComparator stringComparator) {
        return findJEditorPane(cont, text, stringComparator, 0);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) waitJTextComponent(cont, PredicatesJ.of(JEditorPane.class, chooser), index);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser) {
        return waitJEditorPane(cont, chooser, 0);
    }

    public static JEditorPane waitJEditorPane(
            Container cont, String text, StringComparator stringComparator, int index) {
        return waitJEditorPane(
                cont,
                PredicatesJ.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JEditorPane waitJEditorPane(Container cont, String text, StringComparator stringComparator) {
        return waitJEditorPane(cont, text, stringComparator, 0);
    }
}
