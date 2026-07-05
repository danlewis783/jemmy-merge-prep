/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.predicates.PredicatesJ;
import org.netbeans.jemmy.util.StringComparators;

class JEditorPaneOperatorTest {

    private JEditorPane editorPane;
    private JFrame frame;

    @BeforeEach
    void beforeEach() throws Exception {
        EventQueue.invokeAndWait(() -> {
            frame = new JFrame("JFrameOperatorTest");
            editorPane = new JEditorPane();
            editorPane.setText("JEditorPaneOperatorTest");
            editorPane.setName("JEditorPaneOperatorTest");
            frame.getContentPane().add(editorPane);
            frame.setName("JFrameOperatorTest");
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @AfterEach
    void after() {
        EventQueue.invokeLater(() -> {});
        EventQueue.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        });
    }

    @Test
    void testConstructor() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator3 =
                new JEditorPaneOperator(operator1, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertNotNull(operator3);
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertNotNull(operator4);
        JEditorPaneOperator operator5 =
                new JEditorPaneOperator(operator1, "JEditorPaneOperatorTest", StringComparators.strict());
        assertNotNull(operator5);
    }

    @Test
    void testFindJEditorPane() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPane editorPane1 =
                JEditorPaneOperator.findJEditorPane(frame, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertNotNull(editorPane1);
        JEditorPane editorPane2 = JEditorPaneOperator.findJEditorPane(
                frame, "JEditorPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(editorPane2);
    }

    @Test
    void testWaitJEditorPane() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPane editorPane1 =
                JEditorPaneOperator.waitJEditorPane(frame, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertNotNull(editorPane1);
        JEditorPane editorPane2 = JEditorPaneOperator.waitJEditorPane(
                frame, "JEditorPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertNotNull(editorPane2);
    }

    @Test
    void testAddHyperlinkListener() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertNotNull(operator2);
        HyperlinkListenerTest listener = new HyperlinkListenerTest();
        operator2.addHyperlinkListener(listener);
        assertEquals(1, editorPane.getHyperlinkListeners().length);
        operator2.fireHyperlinkUpdate(new HyperlinkEvent("", null, null));
        assertNotNull(listener.event);
        operator2.removeHyperlinkListener(listener);
        assertEquals(0, editorPane.getHyperlinkListeners().length);
    }

    @Test
    void testGetContentType() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertNotNull(operator4);
        assertEquals("text/plain", operator4.getContentType());
        operator4.setContentType("text/html");
        assertEquals("text/html", operator4.getContentType());
        assertEquals(operator4.getContentType(), editorPane.getContentType());
    }

    @Test
    void testGetEditorKit() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertNotNull(operator2);
        EditorKitTest editorKit = new EditorKitTest();
        operator2.setEditorKit(editorKit);
        assertEquals(editorKit, operator2.getEditorKit());
        assertEquals(editorPane.getEditorKit(), operator2.getEditorKit());
    }

    @Test
    void testGetEditorKitForContentType() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertNotNull(operator2);
        EditorKitTest editorKit = new EditorKitTest();
        operator2.setEditorKitForContentType("text/plain", editorKit);
        assertEquals(editorKit, operator2.getEditorKitForContentType("text/plain"));
        assertEquals(
                editorPane.getEditorKitForContentType("text/plain"),
                operator2.getEditorKitForContentType("text/plain"));
    }

    @Test
    void testGetPage() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertNotNull(operator4);

        try {
            String urlA = "https://www.google.com/";
            String urlB = "https://www.google.com/";
            operator4.setPage(new URL(urlA));
            assertEquals(urlA, operator4.getPage().toString());
            assertEquals(urlA, editorPane.getPage().toString());
            operator4.setPage(urlB);
            JEditorPaneOperator operator5 = new JEditorPaneOperator(operator1);
            assertEquals(urlB, operator5.getPage().toString());
            assertEquals(urlB, editorPane.getPage().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRead() {
        JFrameOperator operator1 = new JFrameOperator();
        assertNotNull(operator1);
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertNotNull(operator2);
        operator2.setContentType("text/html");
        operator2.read(new ByteArrayInputStream("<html></html>".getBytes()), HTMLDocument.class);
        assertTrue(editorPane.getText().startsWith("<html>"));
    }

    private class EditorKitTest extends EditorKit {
        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ViewFactory getViewFactory() {
            return null;
        }

        @Override
        public Action[] getActions() {
            return null;
        }

        @Override
        public Caret createCaret() {
            return null;
        }

        @Override
        public Document createDefaultDocument() {
            return new DefaultStyledDocument();
        }

        @Override
        public void read(InputStream in, Document doc, int pos) {}

        @Override
        public void write(OutputStream out, Document doc, int pos, int len) {}

        @Override
        public void read(Reader in, Document doc, int pos) {}

        @Override
        public void write(Writer out, Document doc, int pos, int len) {}
    }

    class HyperlinkListenerTest implements HyperlinkListener {
        private HyperlinkEvent event;

        @Override
        public void hyperlinkUpdate(HyperlinkEvent event) {
            this.event = event;
        }
    }
}
