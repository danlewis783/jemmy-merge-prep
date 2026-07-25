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

import java.awt.EventQueue;
import javax.swing.ToolTipManager;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Shortens {@link ToolTipManager}'s initial and reshow delays for the duration of a test class, so
 * tooltip-heavy tests do not pay the JDK's realistic delay on every tooltip shown. The default
 * {@code initialDelay} is about 750 ms; a test that runs 16 {@code showToolTip} cycles therefore
 * spends roughly 12 s doing nothing but waiting for the mouse to "settle" before each tooltip pops
 * up. This extension trades that realism for headroom: before the class's tests run,
 * {@code initialDelay} drops to {@value #FAST_INITIAL_DELAY_MS} ms and {@code reshowDelay} to
 * {@value #FAST_RESHOW_DELAY_MS} ms, and both are restored to their captured values afterward.
 * {@code dismissDelay} is left untouched - nothing here needs a tooltip to close sooner than the JDK
 * default.
 * <p>
 * {@code ToolTipManager} is Swing state, so both the capture-and-set in {@link #beforeAll} and the
 * restore in {@link #afterAll} run on the event dispatch thread via {@link EventQueue#invokeAndWait}.
 * <p>
 * Opt-in: register only on test classes that actually exercise tooltips, with
 * {@code @ExtendWith(FastToolTips.class)}.
 */
public final class FastToolTips implements BeforeAllCallback, AfterAllCallback {

    private static final int FAST_INITIAL_DELAY_MS = 50;
    private static final int FAST_RESHOW_DELAY_MS = 0;

    private int savedInitialDelay;
    private int savedReshowDelay;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        EventQueue.invokeAndWait(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();
            savedInitialDelay = manager.getInitialDelay();
            savedReshowDelay = manager.getReshowDelay();
            manager.setInitialDelay(FAST_INITIAL_DELAY_MS);
            manager.setReshowDelay(FAST_RESHOW_DELAY_MS);
        });
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        EventQueue.invokeAndWait(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();
            manager.setInitialDelay(savedInitialDelay);
            manager.setReshowDelay(savedReshowDelay);
        });
    }
}
