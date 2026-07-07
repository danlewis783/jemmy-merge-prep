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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator3 =
                new JEditorPaneOperator(operator1, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertThat(operator3).isNotNull();
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertThat(operator4).isNotNull();
        JEditorPaneOperator operator5 =
                new JEditorPaneOperator(operator1, "JEditorPaneOperatorTest", StringComparators.strict());
        assertThat(operator5).isNotNull();
    }

    @Test
    void testFindJEditorPane() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPane editorPane1 =
                JEditorPaneOperator.findJEditorPane(frame, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertThat(editorPane1).isNotNull();
        JEditorPane editorPane2 = JEditorPaneOperator.findJEditorPane(
                frame, "JEditorPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(editorPane2).isNotNull();
    }

    @Test
    void testWaitJEditorPane() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPane editorPane1 =
                JEditorPaneOperator.waitJEditorPane(frame, PredicatesJ.byName("JEditorPaneOperatorTest"));
        assertThat(editorPane1).isNotNull();
        JEditorPane editorPane2 = JEditorPaneOperator.waitJEditorPane(
                frame, "JEditorPaneOperatorTest", StringComparators.caseInsensitiveSubstring());
        assertThat(editorPane2).isNotNull();
    }

    @Test
    void testAddHyperlinkListener() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertThat(operator2).isNotNull();
        HyperlinkListenerTest listener = new HyperlinkListenerTest();
        operator2.addHyperlinkListener(listener);
        assertThat(editorPane.getHyperlinkListeners().length).isEqualTo(1);
        operator2.fireHyperlinkUpdate(new HyperlinkEvent("", null, null));
        assertThat(listener.event).isNotNull();
        operator2.removeHyperlinkListener(listener);
        assertThat(editorPane.getHyperlinkListeners().length).isEqualTo(0);
    }

    @Test
    void testGetContentType() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertThat(operator4).isNotNull();
        assertThat(operator4.getContentType()).isEqualTo("text/plain");
        operator4.setContentType("text/html");
        assertThat(operator4.getContentType()).isEqualTo("text/html");
        assertThat(editorPane.getContentType()).isEqualTo(operator4.getContentType());
    }

    @Test
    void testGetEditorKit() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertThat(operator2).isNotNull();
        EditorKitTest editorKit = new EditorKitTest();
        operator2.setEditorKit(editorKit);
        assertThat(operator2.getEditorKit()).isEqualTo(editorKit);
        assertThat(operator2.getEditorKit()).isEqualTo(editorPane.getEditorKit());
    }

    @Test
    void testGetEditorKitForContentType() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertThat(operator2).isNotNull();
        EditorKitTest editorKit = new EditorKitTest();
        operator2.setEditorKitForContentType("text/plain", editorKit);
        assertThat(operator2.getEditorKitForContentType("text/plain")).isEqualTo(editorKit);
        assertThat(operator2.getEditorKitForContentType("text/plain"))
                .isEqualTo(editorPane.getEditorKitForContentType("text/plain"));
    }

    @Test
    void testGetPage() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator4 = new JEditorPaneOperator(operator1);
        assertThat(operator4).isNotNull();

        try {
            String urlA = "https://www.google.com/";
            String urlB = "https://www.google.com/";
            operator4.setPage(new URL(urlA));
            assertThat(operator4.getPage().toString()).isEqualTo(urlA);
            assertThat(editorPane.getPage().toString()).isEqualTo(urlA);
            operator4.setPage(urlB);
            JEditorPaneOperator operator5 = new JEditorPaneOperator(operator1);
            assertThat(operator5.getPage().toString()).isEqualTo(urlB);
            assertThat(editorPane.getPage().toString()).isEqualTo(urlB);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRead() {
        JFrameOperator operator1 = new JFrameOperator();
        assertThat(operator1).isNotNull();
        JEditorPaneOperator operator2 = new JEditorPaneOperator(operator1);
        assertThat(operator2).isNotNull();
        operator2.setContentType("text/html");
        operator2.read(new ByteArrayInputStream("<html></html>".getBytes()), HTMLDocument.class);
        assertThat(editorPane.getText().startsWith("<html>")).isTrue();
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
