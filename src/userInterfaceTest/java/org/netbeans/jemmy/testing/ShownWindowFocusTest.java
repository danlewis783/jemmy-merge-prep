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

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JemmyStateResetExtension.class)
@Timeout(value=30, unit=TimeUnit.SECONDS)
class ShownWindowFocusTest {
    private static final long LONG_WAIT_MS = 10_000L;

    /** One size for every test's windows, wide enough for the whole title to show. */
    private static final Dimension WINDOW_SIZE = new Dimension(380, 200);

    @AfterEach
    void afterEach() throws InterruptedException, InvocationTargetException {
        TestWindows.disposeAll();
    }

    @Test
    void waitsUntilAShownFrameHasFocus() throws InterruptedException, InvocationTargetException {
        JFrame frame = onQueue(() -> {
            JFrame shown = new JFrame("ShownWindowFocusTest");
            shown.add(new JLabel("focus me"));
            shown.setSize(WINDOW_SIZE);
            TestWindows.place(shown);
            shown.setVisible(true);
            return shown;
        });

        ShownWindowFocus.awaitFocus(LONG_WAIT_MS);

        assertThat(onQueue(ShownWindowFocusTest::focusedWindow)).isSameAs(frame);
    }

    @Test
    void doesNotWaitForAWindowThatCannotTakeFocus() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            JWindow unfocusable = new JWindow();
            unfocusable.setFocusableWindowState(false);
            unfocusable.setSize(50, 50);
            TestWindows.place(unfocusable);
            unfocusable.setVisible(true);
        });

        long started = System.nanoTime();
        ShownWindowFocus.awaitFocus(LONG_WAIT_MS);

        assertThat(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started))
                .as("returned without waiting out the timeout")
                .isLessThan(LONG_WAIT_MS / 2);
    }

    private static @Nullable Window focusedWindow() {
        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
    }
}
