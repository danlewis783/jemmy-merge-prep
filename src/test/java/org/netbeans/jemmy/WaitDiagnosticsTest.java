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
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
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
            assertThat(visited.get())
                    .isLessThanOrEqualTo(WaitDiagnostics.UiCapture.MAX_UNRELATED_COMPONENTS - 1);
            assertThat(capture.warnings)
                    .contains("capture pruned: unrelated component limit reached (128 visited)")
                    .doesNotContain("capture truncated: component limit reached (256 visited)");
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
            assertThat(depthCapture.warnings)
                    .contains("capture truncated: hierarchy depth limit reached (32 levels)");
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
            assertThat(capture.warnings).contains("capture stopped: EDT probe abandoned");
            WaitDiagnostics.UiCapture expired = new WaitDiagnostics.UiCapture(
                    new AtomicBoolean(),
                    System.nanoTime() - TimeUnit.SECONDS.toNanos(1));
            assertThat(expired.component(root, 0)).isNull();
            assertThat(expired.warnings).contains("capture truncated: EDT capture exceeded 300 ms");
            assertThat(visited.get()).isEqualTo(1);
        });
    }

    @Test
    void capturesTheDiagnosticBranchBeforeAnUnrelatedWideFocusBranch() throws Exception {
        EventQueue.invokeAndWait(() -> {
            JPanel root = new JPanel() {
                @Override public boolean isShowing() { return true; }
            };
            JPanel focusBranch = new JPanel();
            for (int i = 0; i < WaitDiagnostics.UiCapture.MAX_COMPONENTS * 2; i++) {
                focusBranch.add(new JLabel("ordinary-" + i));
            }
            JLabel focusOwner = new JLabel("focus owner") {
                @Override public boolean hasFocus() { return true; }
            };
            focusBranch.add(focusOwner);
            root.add(focusBranch);
            JLabel target = new JLabel("current target value");
            target.setName("diagnostic-target");
            root.add(target);

            WaitDiagnostics.UiCapture capture = new WaitDiagnostics.UiCapture(
                    new AtomicBoolean(), System.nanoTime(), focusOwner, target);
            String rendered = render(capture.component(root, 0));

            assertThat(rendered)
                    .contains("name=\"diagnostic-target\"")
                    .contains("text=\"current target value\"")
                    .contains("Focused component ancestry:")
                    .contains("text=\"focus owner\"");
            assertThat(capture.warnings)
                    .contains("capture pruned: unrelated descendants omitted from priority paths")
                    .doesNotContain("capture truncated: component limit reached (256 visited)");
        });
    }

    @Test
    void attachesContextFromWaitsImplementedOutsideJemmyRepeaters() {
        AssertionError failure = new AssertionError("value did not match");
        JLabel component = new JLabel("actual value");

        WaitDiagnostics.attachTo(failure, "field value to equal expected value", component);

        DiagnosticCapture snapshot = WaitDiagnostics.findSnapshot(failure);
        FailedWait waitFailure = WaitDiagnostics.findFailedWait(failure);
        assertThat(snapshot).isNotNull();
        assertThat(waitFailure).isNotNull();
        assertThat(snapshot.renderSummary(waitFailure))
                .contains("Wait failed for:", "field value to equal expected value", "Wait component:");
    }

    @Test
    void capturesBoundedTreeSelectionExpansionAndSiblings() throws Exception {
        EventQueue.invokeAndWait(() -> {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
            DefaultMutableTreeNode selected = new DefaultMutableTreeNode("selected");
            root.add(selected);
            root.add(new DefaultMutableTreeNode("sibling"));
            JTree tree = new JTree(root);
            TreePath rootPath = new TreePath(root.getPath());
            TreePath selectedPath = new TreePath(selected.getPath());
            tree.expandPath(rootPath);
            tree.setSelectionPath(selectedPath);

            String rendered = render(capture().component(tree, 0));

            assertThat(rendered)
                    .contains("selectedPaths=[[root, selected]]")
                    .contains("expandedPaths=[[root]]")
                    .contains("siblings=[selected, sibling]");
        });
    }

    private static WaitDiagnostics.UiCapture capture() {
        return new WaitDiagnostics.UiCapture(new AtomicBoolean(), System.nanoTime());
    }

    private static String render(DiagnosticCapture.ComponentSnapshot component) {
        return new DiagnosticCapture(null,
                DiagnosticCapture.EdtStatus.UNAVAILABLE, null, null, Collections.emptyList(),
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
                    null,
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
    void replacesInlineDiagnosticDetailWithAReportPointer() {
        Throwable failure = new RuntimeException("ordinary failure");
        WaitDiagnostics.attachTo(failure);

        WaitDiagnostics.referenceDiagnosticsReport(failure);

        assertThat(failure.getSuppressed()).singleElement().satisfies(diagnostics ->
                assertThat(diagnostics.getMessage())
                        .isEqualTo("diagnostics report attached; see Standard Error")
                        .doesNotContain("EDT probe:"));
        assertThat(WaitDiagnostics.findSnapshot(failure)).isNotNull();
    }

    @Test
    void retainsCaptureFailure() {
        Throwable failure = new RuntimeException("timeout");
        Throwable diagnosticsFailure = new AssertionError(SENTINEL);

        WaitDiagnostics.attachCaptureFailure(
                failure, diagnosticsFailure);

        assertThat(failure.getSuppressed()).containsExactly(diagnosticsFailure);
    }

    @Test
    void promotesASecondaryUiFailureAndRetainsItsFullStackForAnAttachment() {
        Throwable primary = new AssertionError("primary");
        Throwable secondary = new NullPointerException("secondary");
        secondary.setStackTrace(new StackTraceElement[] {
            new StackTraceElement("java.awt.EventDispatchThread", "run", "EventDispatchThread.java", 90),
            new StackTraceElement("example.ui.SampleView", "refresh", "SampleView.java", 42)
        });

        WaitDiagnostics.attachSecondaryUiFailure(primary, secondary);
        WaitDiagnostics.attachSecondaryUiFailure(primary, secondary);

        assertThat(WaitDiagnostics.findSecondaryUiFailureSummary(primary))
                .isEqualTo("Secondary EDT failure: NullPointerException at example.ui.SampleView.refresh(SampleView.java:42)");
        assertThat(WaitDiagnostics.findSecondaryUiFailureDetail(primary))
                .contains("java.lang.NullPointerException: secondary")
                .contains("at example.ui.SampleView.refresh(SampleView.java:42)");
        assertThat(primary.getSuppressed()).singleElement().satisfies(marker ->
                assertThat(marker.getStackTrace()).isEmpty());
    }

    @Test
    void recordsAnEdtFailureAfterApplicationHandlerInstallation() {
        Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> delegated = new AtomicReference<>();
        NullPointerException secondary = new NullPointerException("secondary");
        AssertionError primary = new AssertionError("primary");
        try {
            Thread.setDefaultUncaughtExceptionHandler((thread, failure) -> delegated.set(failure));
            WaitDiagnostics.installEdtFailureRecorder();
            WaitDiagnostics.clearRecordedEdtFailure();

            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("AWT-EventQueue-0"), secondary);
            WaitDiagnostics.attachRecordedEdtFailure(primary);

            assertThat(delegated.get()).isNull();
            assertThat(WaitDiagnostics.findSecondaryUiFailureSummary(primary))
                    .startsWith("Secondary EDT failure: NullPointerException");

            RuntimeException workerFailure = new RuntimeException("worker failed");
            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("worker-1"), workerFailure);
            assertThat(delegated.get()).isSameAs(workerFailure);
        } finally {
            WaitDiagnostics.clearRecordedEdtFailure();
            Thread.setDefaultUncaughtExceptionHandler(original);
        }
    }

    @Test
    void reportsAnEdtFailureThatWasNotAssociatedWithATestFailure() {
        Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();
        AtomicReference<Throwable> delegated = new AtomicReference<>();
        NullPointerException secondary = new NullPointerException("secondary");
        try {
            Thread.setDefaultUncaughtExceptionHandler((thread, failure) -> delegated.set(failure));
            WaitDiagnostics.installEdtFailureRecorder();
            WaitDiagnostics.clearRecordedEdtFailure();

            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(new Thread("AWT-EventQueue-0"), secondary);
            WaitDiagnostics.reportRecordedEdtFailure();

            assertThat(delegated.get()).isSameAs(secondary);
        } finally {
            WaitDiagnostics.clearRecordedEdtFailure();
            Thread.setDefaultUncaughtExceptionHandler(original);
        }
    }

}
