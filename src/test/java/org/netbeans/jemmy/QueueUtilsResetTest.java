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

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.EnumSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

// mutates the queue installation and the JemmyContext singleton's dispatching model, never run in parallel
@Isolated
class QueueUtilsResetTest {

    private final JemmyContext context = JemmyContext.getInstance();

    @BeforeEach
    void beforeEach() {
        context.resetToDefaults();
    }

    @AfterEach
    void afterEach() {
        context.resetToDefaults();
    }

    private static EventQueue systemEventQueue() {
        return Toolkit.getDefaultToolkit().getSystemEventQueue();
    }

    /** Forces {@code QueueUtils} to initialize from the current dispatching model. */
    private static void dispatchSomething() {
        QueueTool.getInstance().runOnQueue(() -> {});
    }

    @Test
    void firstDispatchInstallsTheJemmyQueueUnderTheDefaultModel() {
        dispatchSomething();

        assertThat(systemEventQueue())
                .as("check that the default model (with Shortcut) installs the JemmyQueue on first dispatch")
                .isInstanceOf(JemmyQueue.class);
    }

    @Test
    void resetUninstallsTheJemmyQueueAndReinitializesOnNextDispatch() {
        dispatchSomething();
        assertThat(systemEventQueue()).isInstanceOf(JemmyQueue.class);

        QueueUtils.reset();

        assertThat(systemEventQueue())
                .as("check that the reset pops the JemmyQueue off the system queue stack")
                .isNotInstanceOf(JemmyQueue.class);

        dispatchSomething();

        assertThat(systemEventQueue())
                .as("check that dispatching after a reset re-reads the model and reinstalls")
                .isInstanceOf(JemmyQueue.class);
    }

    @Test
    void modelSwitchResyncsTheQueueInstallation() {
        dispatchSomething();
        assertThat(systemEventQueue()).isInstanceOf(JemmyQueue.class);

        context.installDriversAndSetDispatchingModel(EnumSet.of(DispatchingModel.Queue));
        dispatchSomething();

        assertThat(systemEventQueue())
                .as("check that switching to a model without Shortcut uninstalls the JemmyQueue")
                .isNotInstanceOf(JemmyQueue.class);

        context.resetToDefaults();
        dispatchSomething();

        assertThat(systemEventQueue())
                .as("check that restoring the default model reinstalls the JemmyQueue")
                .isInstanceOf(JemmyQueue.class);
    }
}
