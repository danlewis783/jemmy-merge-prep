/*
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation, with the "Classpath"
 * exception as provided in the LICENSE file that accompanied this code.
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
 */
package org.netbeans.jemmy.operators;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.IllegalComponentStateException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Objects;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Exercises {@code JEditorPaneOperator.clickOnReference}, ported from openjdk/jemmy-v2 (CODETOOLS-7902156), with
 * and without an enclosing scroll pane. The HTML fixtures link to each other by named anchors.
 */
// UI fixtures are created on the EDT in beforeEach or the test body; NullAway cannot see through invokeAndWait
@SuppressWarnings("NullAway.Init")
class JEditorPaneClickOnReferenceTest {

    private static final String PAGE1 = "page1";
    private static final String PAGE2 = "page2";
    private static final String PAGE1_TEXT = "hi";
    private static final String PAGE2_TEXT = "hello";

    private final URL page1Url =
            Objects.requireNonNull(getClass().getResource("resources/page1.html"), "page1.html fixture");
    private final HyperlinkListener listener = event -> {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                ((JEditorPane) event.getSource()).setPage(event.getURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private JFrame frame;
    private JEditorPane editorPane;

    @BeforeEach
    void beforeEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame();
            frame.setPreferredSize(new Dimension(800, 600));
        });
    }

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            editorPane.removeHyperlinkListener(listener);
            frame.setVisible(false);
            frame.dispose();
        });
    }

    @Test
    void clickOnReferenceWithScrollPane() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            editorPane = newEditorPane();
            frame.getContentPane().add(new JScrollPane(editorPane), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        JEditorPaneOperator operator = new JEditorPaneOperator(new JFrameOperator(frame));
        checkPageLoaded(operator, PAGE1, PAGE1_TEXT);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> operator.clickOnReference(PAGE1))
                .withMessageContaining("Reference page1 doesn't exist in the document");

        operator.addHyperlinkListener(listener);
        // short page: the anchor is in the initial view
        operator.clickOnReference(PAGE2);
        checkPageLoaded(operator, PAGE2, PAGE2_TEXT);

        // long page: the anchor must be scrolled to first
        operator.clickOnReference(PAGE1);
        operator.waitStateOnQueue(op -> {
            URL page = ((JEditorPane) op.getSource()).getPage();

            return (page != null) && page.toString().contains(PAGE1);
        });
    }

    @Test
    void clickOnReferenceWithoutScrollPane() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            editorPane = newEditorPane();
            frame.getContentPane().add(editorPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        JEditorPaneOperator operator = new JEditorPaneOperator(new JFrameOperator(frame));
        checkPageLoaded(operator, PAGE1, PAGE1_TEXT);
        operator.addHyperlinkListener(listener);

        // reference within the visible area works without a scroll pane
        operator.clickOnReference(PAGE2);
        checkPageLoaded(operator, PAGE2, PAGE2_TEXT);

        // reference outside the visible area cannot be reached without one
        assertThatExceptionOfType(IllegalComponentStateException.class)
                .isThrownBy(() -> operator.clickOnReference(PAGE1))
                .withMessage("Component doesn't contain JScrollPane and Reference is out of visible area");
    }

    private JEditorPane newEditorPane() {
        try {
            JEditorPane pane = new JEditorPane(page1Url);
            pane.setEditable(false);

            return pane;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void checkPageLoaded(JEditorPaneOperator operator, String page, String text) {
        operator.waitStateOnQueue(op -> {
            URL current = ((JEditorPane) op.getSource()).getPage();

            return (current != null) && current.toString().contains(page);
        });
        operator.waitStateOnQueue(op -> ((JEditorPane) op.getSource()).getText().contains(text));
    }
}
