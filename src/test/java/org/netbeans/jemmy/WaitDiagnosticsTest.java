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
package org.netbeans.jemmy;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import static org.assertj.core.api.Assertions.assertThat;

@Isolated
class WaitDiagnosticsTest {
    private static final String SENTINEL = "capture failure details";

    @Test
    void boundsDocumentAndSelectionReadsBeforeAllocatingText() throws Exception {
        EventQueue.invokeAndWait(() -> {
            AtomicBoolean checking = new AtomicBoolean();
            AtomicInteger reads = new AtomicInteger();
            PlainDocument document = new PlainDocument() {
                @Override public String getText(int offset, int length) throws BadLocationException {
                    if (checking.get()) {
                        assertThat(length).isLessThanOrEqualTo(WaitDiagnostics.UiCapture.MAX_VALUE_LENGTH);
                        reads.incrementAndGet();
                    }
                    return super.getText(offset, length);
                }
            };
            JTextArea editor = new JTextArea(document) {
                @Override public String getText() { throw new AssertionError("unbounded document read"); }
                @Override public String getSelectedText() { throw new AssertionError("unbounded selection read"); }
            };
            char[] chars = new char[100_000];
            Arrays.fill(chars, 'x');
            try {
                document.insertString(0, new String(chars), null);
            } catch (BadLocationException e) {
                throw new AssertionError(e);
            }
            editor.selectAll();
            checking.set(true);
            String tree = render(capture().component(editor, 0));
            assertThat(reads.get()).isEqualTo(2);
            assertThat(tree).contains("text=\"", "selectedText=\"", "...");
            assertThat(tree).doesNotContain(new String(new char[501]).replace('\0', 'x'));
        });
    }

    @Test
    void boundsHierarchyWidthDepthAndStoredValues() throws Exception {
        EventQueue.invokeAndWait(() -> {
            AtomicInteger visited = new AtomicInteger();
            JPanel wide = new JPanel();
            char[] chars = new char[10_000];
            Arrays.fill(chars, 'x');
            for (int i = 0; i < WaitDiagnostics.UiCapture.MAX_COMPONENTS * 2; i++) {
                JLabel label = new JLabel(new String(chars)) {
                    @Override public String getName() { visited.incrementAndGet(); return super.getName(); }
                };
                label.setToolTipText(new String(chars));
                wide.add(label);
            }
            WaitDiagnostics.UiCapture capture = capture();
            assertThat(capture.component(wide, 0)).isNotNull();
            assertThat(visited.get()).isLessThanOrEqualTo(WaitDiagnostics.UiCapture.MAX_COMPONENTS - 1);
            assertThat(capture.warnings).contains("component capture truncated by hierarchy or time limit");
            String values = render(capture().component(wide.getComponent(0), 0));
            assertThat(values).contains("...").doesNotContain(new String(chars));

            visited.set(0);
            JPanel deep = new JPanel();
            JPanel parent = deep;
            for (int i = 0; i < WaitDiagnostics.UiCapture.MAX_DEPTH * 2; i++) {
                JPanel child = new JPanel() {
                    @Override public String getName() { visited.incrementAndGet(); return super.getName(); }
                };
                parent.add(child);
                parent = child;
            }
            WaitDiagnostics.UiCapture depthCapture = capture();
            assertThat(depthCapture.component(deep, 0)).isNotNull();
            assertThat(visited.get()).isLessThanOrEqualTo(WaitDiagnostics.UiCapture.MAX_DEPTH - 1);
            assertThat(depthCapture.warnings).contains("component capture truncated by hierarchy or time limit");
        });
    }

    @Test
    void stopsTraversalWhenProbeIsAbandonedOrDeadlineExpires() throws Exception {
        EventQueue.invokeAndWait(() -> {
            AtomicBoolean abandoned = new AtomicBoolean();
            AtomicInteger visited = new AtomicInteger();
            JPanel root = new JPanel();
            for (int i = 0; i < 10; i++) {
                root.add(new JLabel() {
                    @Override public String getName() {
                        visited.incrementAndGet();
                        abandoned.set(true);
                        return null;
                    }
                });
            }
            WaitDiagnostics.UiCapture capture = new WaitDiagnostics.UiCapture(
                    abandoned, System.nanoTime());
            assertThat(capture.component(root, 0)).isNotNull();
            assertThat(visited.get()).isEqualTo(1);
            WaitDiagnostics.UiCapture expired = new WaitDiagnostics.UiCapture(
                    new AtomicBoolean(),
                    System.nanoTime() - TimeUnit.SECONDS.toNanos(1));
            assertThat(expired.component(root, 0)).isNull();
            assertThat(visited.get()).isEqualTo(1);
        });
    }

    private static WaitDiagnostics.UiCapture capture() {
        return new WaitDiagnostics.UiCapture(new AtomicBoolean(), System.nanoTime());
    }

    private static String render(WaitDiagnosticSnapshot.ComponentSnapshot component) {
        return new WaitDiagnosticSnapshot(null, null, null, null,
                WaitDiagnosticSnapshot.EdtStatus.UNAVAILABLE, null, null, Collections.emptyList(),
                null, null, null, Collections.singletonList(component), "unknown", Collections.emptyList()).renderComponentTree();
    }

    @Test
    void diagnosticsAreEnabledByDefault() {
        String configured = System.getProperty(WaitDiagnostics.ENABLED_PROPERTY);
        try {
            System.clearProperty(WaitDiagnostics.ENABLED_PROPERTY);

            assertThat(WaitDiagnostics.isEnabled()).isTrue();
        } finally {
            restoreDiagnosticsProperty(configured);
        }
    }

    @Test
    void disabledDiagnosticsUseTheFallbackTimeoutAndDoNotAttach() {
        String configured = System.getProperty(WaitDiagnostics.ENABLED_PROPERTY);
        try {
            System.setProperty(WaitDiagnostics.ENABLED_PROPERTY, "false");
            RuntimeException ordinaryFailure = new RuntimeException("ordinary failure");

            TimeoutExpiredException timeout = WaitDiagnostics.timeoutFailure(
                    "fallback timeout",
                    TimeoutKey.Waiter_WaitingTime,
                    1L,
                    "target",
                    null);
            WaitDiagnostics.attachTo(ordinaryFailure);

            assertThat(WaitDiagnostics.isEnabled()).isFalse();
            assertThat(timeout).hasMessage("fallback timeout");
            assertThat(timeout.getSuppressed()).isEmpty();
            assertThat(ordinaryFailure.getSuppressed()).isEmpty();
        } finally {
            restoreDiagnosticsProperty(configured);
        }
    }

    private static void restoreDiagnosticsProperty(String configured) {
        if (configured == null) {
            System.clearProperty(WaitDiagnostics.ENABLED_PROPERTY);
        } else {
            System.setProperty(WaitDiagnostics.ENABLED_PROPERTY, configured);
        }
    }

    @Test
    void findsDiagnosticsInFailureMessage() {
        Throwable failure = new RuntimeException("failure\n--- wait diagnostics ---\nmouse: unavailable");

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void findsDiagnosticsInCause() {
        Throwable failure = new RuntimeException(
                "wrapper",
                new RuntimeException("--- wait diagnostics ---")
        );

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void findsDiagnosticsInSuppressedFailure() {
        Throwable failure = new RuntimeException("failure");
        failure.addSuppressed(new RuntimeException("--- wait diagnostics ---"));

        assertThat(WaitDiagnostics.isPresentIn(failure)).isTrue();
    }

    @Test
    void reportsDiagnosticsAbsent() {
        Throwable failure = new RuntimeException("ordinary failure");

        assertThat(WaitDiagnostics.isPresentIn(failure)).isFalse();
    }

    @Test
    void attachesStacklessDiagnosticsOnlyOnce() {
        Throwable failure = new RuntimeException("ordinary failure");

        WaitDiagnostics.attachTo(failure);
        WaitDiagnostics.attachTo(failure);

        assertThat(failure.getSuppressed()).hasSize(1);
        assertThat(failure.getSuppressed()[0].getMessage())
                .startsWith("--- wait diagnostics ---")
                .contains("EDT probe:");
        assertThat(failure.getSuppressed()[0].getStackTrace()).isEmpty();
    }

    @Test
    void retainsCaptureFailure() {
        Throwable failure = new RuntimeException("timeout");
        Throwable diagnosticsFailure = new AssertionError(SENTINEL);

        WaitDiagnostics.attachCaptureFailure(
                failure, diagnosticsFailure);

        assertThat(failure.getSuppressed()).containsExactly(diagnosticsFailure);
    }

}
