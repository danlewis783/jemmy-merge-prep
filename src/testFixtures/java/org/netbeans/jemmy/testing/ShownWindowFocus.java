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
 */
package org.netbeans.jemmy.testing;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.Nullable;

/**
 * Waits for the focus that showing a window requests to arrive. AWT asks for focus for every
 * focusable window it shows, but the platform grants it asynchronously: on Windows almost at
 * once, on X11 without a window manager sometimes well after the test has moved on. A late grant
 * then overrides whatever the test did meanwhile, such as focusing another window, and until it
 * arrives no window is focused, so Swing suppresses tooltips ("activeApplication" mode).
 */
final class ShownWindowFocus {
    private static final long POLL_INTERVAL_MS = 20L;

    /** The window most recently shown; accessed on the event dispatch thread only. */
    private static @Nullable WeakReference<Window> lastShown;

    /** Whether {@link #lastShown} has gained focus since it was shown; EDT only. */
    private static boolean lastShownGainedFocus;

    private static final AtomicBoolean installed = new AtomicBoolean();

    private ShownWindowFocus() {}

    /** Starts tracking shown windows; later calls do nothing. */
    static void install() {
        if (installed.compareAndSet(false, true)) {
            AWTEventListener listener = ShownWindowFocus::track;
            Toolkit.getDefaultToolkit().addAWTEventListener(
                    listener, AWTEvent.COMPONENT_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK);
        }
    }

    private static void track(AWTEvent event) {
        if ((event.getID() == ComponentEvent.COMPONENT_SHOWN) && (event.getSource() instanceof Window)) {
            lastShown = new WeakReference<>((Window) event.getSource());
            lastShownGainedFocus = false;
        } else if ((event.getID() == WindowEvent.WINDOW_GAINED_FOCUS) && (event.getSource() == shownWindow())) {
            lastShownGainedFocus = true;
        }
    }

    /**
     * Waits up to {@code timeoutMillis} for the most recently shown window to receive the focus
     * its showing requested. Returns at once when that window is gone, hidden or cannot take
     * focus; after the timeout it returns without failing, leaving the test to report any
     * consequence.
     */
    static void awaitFocus(long timeoutMillis) throws InterruptedException, InvocationTargetException {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        AtomicBoolean settled = new AtomicBoolean();
        while (true) {
            EventQueue.invokeAndWait(() -> settled.set(isSettled()));
            if (settled.get() || (System.nanoTime() - deadline) >= 0L) {
                return;
            }
            Thread.sleep(POLL_INTERVAL_MS);
        }
    }

    private static boolean isSettled() {
        Window window = shownWindow();
        if ((window == null)
                || !window.isShowing()
                || !window.isFocusableWindow()
                || !window.isAutoRequestFocus()
                || lastShownGainedFocus) {
            return true;
        }
        // the focus event can be dispatched before the window's COMPONENT_SHOWN event
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() == window;
    }

    private static @Nullable Window shownWindow() {
        WeakReference<Window> reference = lastShown;
        return (reference == null) ? null : reference.get();
    }
}
