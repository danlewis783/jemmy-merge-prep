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

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.jetbrains.annotations.Nullable;

/**
 * Captures the environment at the moment a wait times out, as text, so a failure on a machine
 * whose artifacts cannot be copied off is still diagnosable from the exception message in the
 * test report alone: the EDT's stack at the timeout instant, whether the EDT responds to a short
 * probe, the window/focus picture, and the pointer position. Runs only on the timeout path —
 * never on success — and never throws: a diagnostics problem must not mask the timeout it is
 * describing.
 */
public final class WaitDiagnostics {
    private static final long EDT_PROBE_TIMEOUT_MS = 300L;

    private WaitDiagnostics() {
        // non-instantiable utility class
    }

    public static String capture() {
        StringBuilder out = new StringBuilder("--- wait diagnostics ---");
        try {
            captureInto(out);
        } catch (RuntimeException e) {
            out.append("\n(diagnostics failed: ").append(e).append(')');
        }

        return out.toString();
    }

    private static void captureInto(StringBuilder out) {
        // the EDT stack first: this is the moment-of-timeout truth, before the probe below
        // gives the EDT a chance to move on
        String stackAtTimeout = edtStack();

        out.append("\nmouse: ").append(mousePosition());

        // one probe task measures responsiveness AND reads window/focus state on the EDT, so a
        // responsive EDT yields a consistent picture with no off-thread reads
        AtomicReference<String> onQueueState = new AtomicReference<>();
        CountDownLatch done = new CountDownLatch(1);
        long probeStart = System.nanoTime();
        EventQueue.invokeLater(() -> {
            onQueueState.set(windowsAndFocus());
            done.countDown();
        });

        if (awaitPreservingInterrupt(done)) {
            long probeMillis = (System.nanoTime() - probeStart) / 1_000_000L;
            out.append("\nEDT probe: responded in ").append(probeMillis).append(" ms");
            out.append(onQueueState.get());
        } else {
            out.append("\nEDT probe: NOT responding within ")
                    .append(EDT_PROBE_TIMEOUT_MS)
                    .append(" ms");
            AWTEvent front = Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent();
            out.append("\nfront of event queue: ").append(front);
            // diagnostic-only fallback: the EDT is unresponsive, so this is the only way to see
            // anything; the reads are unsynchronized and labeled as such
            out.append("\n(window/focus state read off-thread; may be inconsistent)");
            out.append(windowsAndFocus());
        }

        out.append("\nEDT stack at timeout:\n").append(stackAtTimeout);
    }

    private static boolean awaitPreservingInterrupt(CountDownLatch done) {
        try {
            return done.await(EDT_PROBE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // a JUnit @Timeout interrupt may land mid-capture; keep it pending for the caller
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private static String mousePosition() {
        try {
            PointerInfo pointer = MouseInfo.getPointerInfo();

            return (pointer != null) ? pointer.getLocation().toString() : "(no pointer)";
        } catch (RuntimeException e) {
            return "(unavailable: " + e + ')';
        }
    }

    private static String windowsAndFocus() {
        StringBuilder sb = new StringBuilder();
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        sb.append("\nfocus: owner=")
                .append(brief(focusManager.getFocusOwner()))
                .append(", focusedWindow=")
                .append(brief(focusManager.getFocusedWindow()))
                .append(", activeWindow=")
                .append(brief(focusManager.getActiveWindow()));

        Window[] windows = Window.getWindows();
        sb.append("\nwindows (").append(windows.length).append("):");
        for (Window window : windows) {
            sb.append("\n  ").append(brief(window));
            sb.append(window.isShowing() ? " showing" : " hidden");
            if (window.isShowing()) {
                sb.append(" bounds=").append(window.getBounds());
            }

            if (window.isFocused()) {
                sb.append(" FOCUSED");
            }

            if (window.isActive()) {
                sb.append(" ACTIVE");
            }
        }

        return sb.toString();
    }

    private static String brief(java.awt.@Nullable Component component) {
        if (component == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder(component.getClass().getSimpleName());
        String name = component.getName();
        if (name != null && !name.isEmpty()) {
            sb.append(" name=\"").append(name).append('"');
        }

        String title = null;
        if (component instanceof Frame) {
            title = ((Frame) component).getTitle();
        } else if (component instanceof Dialog) {
            title = ((Dialog) component).getTitle();
        }

        if (title != null && !title.isEmpty()) {
            sb.append(" \"").append(title).append('"');
        }

        return sb.toString();
    }

    private static String edtStack() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            if (entry.getKey().getName().startsWith("AWT-EventQueue")) {
                sb.append("  ").append(entry.getKey().getName()).append(':');
                for (StackTraceElement frame : entry.getValue()) {
                    sb.append("\n    at ").append(frame);
                }
            }
        }

        return (sb.length() > 0) ? sb.toString() : "  (no AWT-EventQueue thread found)";
    }
}
