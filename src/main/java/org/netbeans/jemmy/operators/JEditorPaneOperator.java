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
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Predicate;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.predicates.ComponentPredicates;
import org.netbeans.jemmy.predicates.JTextComponentByTextPredicate;
import org.netbeans.jemmy.util.EmptyVisualizer;
import org.netbeans.jemmy.util.StringComparator;

public class JEditorPaneOperator extends JTextComponentOperator {
    public static JEditorPaneOperator waitFor(ContainerOperator cont) {
        return waitFor(cont, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont) {
        this(cont, 0);
    }

    /**
     * @deprecated Use {@link #of(JEditorPane)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(JEditorPane b) {
        super(b);
    }

    public static JEditorPaneOperator of(JEditorPane b) {
        return new JEditorPaneOperator(b);
    }

    public static JEditorPaneOperator waitFor(ContainerOperator cont, int index) {
        return new JEditorPaneOperator(
                (JEditorPane) waitComponent(cont, ComponentPredicates.of(JEditorPane.class), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, int)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont, int index) {
        this((JEditorPane) waitComponent(cont, ComponentPredicates.of(JEditorPane.class), index));
    }

    public static JEditorPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser) {
        return waitFor(cont, chooser, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont, Predicate<Component> chooser) {
        this(cont, chooser, 0);
    }

    public static JEditorPaneOperator waitFor(ContainerOperator cont, String text, StringComparator stringComparator) {
        return waitFor(cont, text, stringComparator, 0);
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator) {
        this(cont, text, stringComparator, 0);
    }

    public static JEditorPaneOperator waitFor(ContainerOperator cont, Predicate<Component> chooser, int index) {
        return new JEditorPaneOperator(
                (JEditorPane) cont.waitSubComponent(ComponentPredicates.of(JEditorPane.class, chooser), index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, Predicate, int)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont, Predicate<Component> chooser, int index) {
        this((JEditorPane) cont.waitSubComponent(ComponentPredicates.of(JEditorPane.class, chooser), index));
    }

    public static JEditorPaneOperator waitFor(
            ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        return new JEditorPaneOperator((JEditorPane) waitComponent(
                cont,
                ComponentPredicates.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index));
    }

    /**
     * @deprecated Use {@link #waitFor(ContainerOperator, String, StringComparator, int)} instead.
     */
    @Deprecated
    public JEditorPaneOperator(ContainerOperator cont, String text, StringComparator stringComparator, int index) {
        this((JEditorPane) waitComponent(
                cont,
                ComponentPredicates.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
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
        JScrollPane scroll = (JScrollPane) getContainer(ComponentPredicates.of(JScrollPane.class));
        if (scroll != null) {
            JScrollPaneOperator scroller = JScrollPaneOperator.of(scroll);
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
        int pos = QueueTool.getInstance().callOnQueue(() -> {
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
        });
        if (pos == -1) {
            throw new IllegalArgumentException(
                    "Reference " + reference + " doesn't exist in the document " + getDocument() + ".");
        }

        return pos;
    }

    public void scrollToReference(String reference) {
        QueueTool.getInstance().runOnQueue(() -> ((JEditorPane) getSource()).scrollToReference(reference));
    }

    public void addHyperlinkListener(HyperlinkListener hyperlinkListener) {
        QueueTool.getInstance().runOnQueue(() -> ((JEditorPane) getSource()).addHyperlinkListener(hyperlinkListener));
    }

    public void fireHyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
        QueueTool.getInstance().runOnQueue(() -> ((JEditorPane) getSource()).fireHyperlinkUpdate(hyperlinkEvent));
    }

    public String getContentType() {
        return QueueTool.getInstance().callOnQueue(() -> ((JEditorPane) getSource()).getContentType());
    }

    public EditorKit getEditorKit() {
        return QueueTool.getInstance().callOnQueue(() -> ((JEditorPane) getSource()).getEditorKit());
    }

    public EditorKit getEditorKitForContentType(String string) {
        return QueueTool.getInstance()
                .callOnQueue(() -> ((JEditorPane) getSource()).getEditorKitForContentType(string));
    }

    public URL getPage() {
        return QueueTool.getInstance().callOnQueue(() -> ((JEditorPane) getSource()).getPage());
    }

    public void read(InputStream inputStream, Object object) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JEditorPane) getSource()).read(inputStream, object);
            } catch (IOException e) {
                throw new JemmyException("Exception when reading", e);
            }
        });
    }

    public void removeHyperlinkListener(HyperlinkListener hyperlinkListener) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JEditorPane) getSource()).removeHyperlinkListener(hyperlinkListener));
    }

    public void setContentType(String string) {
        QueueTool.getInstance().runOnQueue(() -> ((JEditorPane) getSource()).setContentType(string));
    }

    public void setEditorKit(EditorKit editorKit) {
        QueueTool.getInstance().runOnQueue(() -> ((JEditorPane) getSource()).setEditorKit(editorKit));
    }

    public void setEditorKitForContentType(String string, EditorKit editorKit) {
        QueueTool.getInstance()
                .runOnQueue(() -> ((JEditorPane) getSource()).setEditorKitForContentType(string, editorKit));
    }

    public void setPage(String string) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JEditorPane) getSource()).setPage(string);
            } catch (IOException e) {
                throw new JemmyException("Exception when setting page", e);
            }
        });
    }

    public void setPage(URL uRL) {
        QueueTool.getInstance().runOnQueue(() -> {
            try {
                ((JEditorPane) getSource()).setPage(uRL);
            } catch (IOException e) {
                throw new JemmyException("Exception when setting page", e);
            }
        });
    }

    public static @Nullable JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) findJTextComponent(cont, ComponentPredicates.of(JEditorPane.class, chooser), index);
    }

    public static @Nullable JEditorPane findJEditorPane(Container cont, Predicate<Component> chooser) {
        return findJEditorPane(cont, chooser, 0);
    }

    public static @Nullable JEditorPane findJEditorPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return findJEditorPane(
                cont,
                ComponentPredicates.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static @Nullable JEditorPane findJEditorPane(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return findJEditorPane(cont, text, stringComparator, 0);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser, int index) {
        return (JEditorPane) waitJTextComponent(cont, ComponentPredicates.of(JEditorPane.class, chooser), index);
    }

    public static JEditorPane waitJEditorPane(Container cont, Predicate<Component> chooser) {
        return waitJEditorPane(cont, chooser, 0);
    }

    public static JEditorPane waitJEditorPane(
            Container cont, @Nullable String text, StringComparator stringComparator, int index) {
        return waitJEditorPane(
                cont,
                ComponentPredicates.of(JEditorPane.class, new JTextComponentByTextPredicate(text, stringComparator)),
                index);
    }

    public static JEditorPane waitJEditorPane(
            Container cont, @Nullable String text, StringComparator stringComparator) {
        return waitJEditorPane(cont, text, stringComparator, 0);
    }
}
