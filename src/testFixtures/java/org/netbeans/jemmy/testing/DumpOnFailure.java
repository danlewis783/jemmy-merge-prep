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
package org.netbeans.jemmy.testing;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.TextComponent;
import java.awt.Window;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.accessibility.AccessibleContext;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.netbeans.jemmy.WaitDiagnostics;

/**
 * Prints a snapshot of the live component hierarchy to stderr when a UI test fails, so post-mortems can see which
 * component held focus, what was visible, and what text was selected at failure time. Implemented as a
 * {@link TestExecutionExceptionHandler} rather than a {@code TestWatcher} so the snapshot is taken before
 * {@code @AfterEach} tears the UI down.
 * <p>
 * Appends {@link WaitDiagnostics#capture()} at the end of the dump. Jemmy's own timeout exceptions
 * ({@code QueueTool}, {@code Repeater}, and the waits built on them) already embed
 * {@link WaitDiagnostics#capture()} in their message, but a JUnit {@code @Timeout} interrupts the test thread
 * directly and bypasses that enrichment entirely: its failure carries only a stack trace at the interrupt point.
 * Appending the capture here means a {@code @Timeout} failure still gets the EDT stack, an EDT-responsiveness
 * probe, a window/focus summary, and the mouse position, in addition to - not instead of - this class's own dump,
 * whose window tree goes deeper than {@link WaitDiagnostics}' summary.
 * <p>
 * The compact {@link WaitDiagnostics} capture is also attached to the failure as a suppressed throwable:
 * failure-summary views (the Gradle HTML report's "Failure details", Jenkins test pages) render only the
 * exception's own message and stack trace, not stderr, and suppressed throwables print inline there. The deep
 * window-tree dump stays on stderr (the "error output" tab), where its size is not a problem.
 * <p>
 * Register with {@code @ExtendWith(DumpOnFailure.class)}.
 */
public final class DumpOnFailure implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable cause) throws Throwable {
        StringBuilder dump = new StringBuilder();
        dump.append("===== DumpOnFailure: ")
                .append(context.getDisplayName())
                .append(" =====\n")
                .append("look and feel: ")
                .append(UIManager.getLookAndFeel().getClass().getSimpleName())
                .append('\n');
        boolean onQueue = snapshotOnQueue(dump);
        if (!onQueue) {
            dump.append("(event queue unresponsive for 5 s; state read off the dispatch thread)\n");
            snapshot(dump);
        }

        String diagnostics = WaitDiagnostics.capture();
        dump.append("===== end DumpOnFailure =====\n").append(diagnostics);
        System.err.println(dump);

        cause.addSuppressed(new Diagnostics(diagnostics));

        throw cause;
    }

    /** Rides the compact diagnostics into failure-summary views; not an error in its own right. */
    private static final class Diagnostics extends Throwable {
        Diagnostics(String diagnostics) {
            // no cause, no suppression of its own, no stack trace: renders as just the text
            super(diagnostics, null, false, false);
        }
    }

    private static boolean snapshotOnQueue(StringBuilder dump) {
        CountDownLatch done = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            snapshot(dump);
            done.countDown();
        });
        try {
            return done.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return false;
        }
    }

    private static void snapshot(StringBuilder dump) {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        dump.append("focus owner: ")
                .append(describe(focusManager.getFocusOwner()))
                .append('\n')
                .append("focused window: ")
                .append(describe(focusManager.getFocusedWindow()))
                .append('\n')
                .append("active window: ")
                .append(describe(focusManager.getActiveWindow()))
                .append('\n');
        for (Window window : Window.getWindows()) {
            dump.append("window: ").append(describe(window)).append('\n');
            appendChildren(dump, window, 1);
        }
    }

    private static void appendChildren(StringBuilder dump, Container container, int depth) {
        for (Component child : container.getComponents()) {
            for (int i = 0; i < depth; i++) {
                dump.append("  ");
            }

            dump.append(describe(child)).append('\n');
            if (child instanceof Container) {
                appendChildren(dump, (Container) child, depth + 1);
            }
        }
    }

    private static String describe(Component comp) {
        if (comp == null) {
            return "null";
        }

        StringBuilder line = new StringBuilder();
        line.append(comp.getClass().getSimpleName());
        if (comp.getName() != null) {
            line.append(" name=\"").append(comp.getName()).append('"');
        }

        line.append(" bounds=[")
                .append(comp.getX())
                .append(',')
                .append(comp.getY())
                .append(' ')
                .append(comp.getWidth())
                .append('x')
                .append(comp.getHeight())
                .append(']');
        line.append(comp.isVisible() ? " visible" : " !visible")
                .append(comp.isShowing() ? " showing" : " !showing")
                .append(comp.isEnabled() ? " enabled" : " !enabled");
        if (comp.hasFocus()) {
            line.append(" FOCUSED");
        }

        AccessibleContext accessibleContext = comp.getAccessibleContext();
        if (accessibleContext != null) {
            if (accessibleContext.getAccessibleName() != null) {
                line.append(" accName=\"")
                        .append(accessibleContext.getAccessibleName())
                        .append('"');
            }

            if (accessibleContext.getAccessibleDescription() != null) {
                line.append(" accDesc=\"")
                        .append(accessibleContext.getAccessibleDescription())
                        .append('"');
            }
        }

        String selectedText = null;
        if (comp instanceof JTextComponent) {
            selectedText = ((JTextComponent) comp).getSelectedText();
        } else if (comp instanceof TextComponent) {
            selectedText = ((TextComponent) comp).getSelectedText();
        }

        if ((selectedText != null) && !selectedText.isEmpty()) {
            line.append(" selectedText=\"").append(selectedText).append('"');
        }

        return line.toString();
    }
}
