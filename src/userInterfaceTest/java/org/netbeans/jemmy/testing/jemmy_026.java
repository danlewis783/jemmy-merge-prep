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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.TimeoutOverride;
import org.netbeans.jemmy.Timeouts;

@Disabled // TODO test too flaky for CI
class jemmy_026 {
    private TimeoutOverride override;

    @BeforeEach
    void beforeEach() {
        override = Timeouts.override(TimeoutKey.QueueTool_WaitQueueEmptyTimeout, 3000L);
    }

    @AfterEach
    void after() {
        override.cancel();
    }

    @Test
    void doit() throws Exception {
        QueueTool qt = QueueTool.getInstance();

        qt.waitEmpty(1000);
        SwingUtilities.invokeLater(new Sleeper("A", 1000));
        SwingUtilities.invokeLater(new Sleeper("B", 1000));
        Thread.sleep(500);
        qt.waitEmpty();
        qt.waitEmpty(1000);
        SwingUtilities.invokeLater(new Sleeper("C", 1000));
        SwingUtilities.invokeLater(new Sleeper("D", 1000));
        SwingUtilities.invokeLater(new Sleeper("E", 1000));
        SwingUtilities.invokeLater(new Sleeper("F", 1000));

        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(qt::waitEmpty)
                .withMessageContaining("timeout \"QueueTool_WaitQueueEmptyTimeout\" (3000 ms) exceeded after (");

        qt.waitEmpty(1000);
        SwingUtilities.invokeLater(new Sleeper("G", 2000));
        SwingUtilities.invokeLater(new Sleeper("H", 1000));
        qt.waitEmpty(500L);
        qt.waitEmpty(1000);
        SwingUtilities.invokeLater(new Sleeper("I", 1000));
        SwingUtilities.invokeLater(new Sleeper("J", 1000));
        SwingUtilities.invokeLater(new Sleeper("K", 1000));
        SwingUtilities.invokeLater(new Sleeper("L", 1000));
        SwingUtilities.invokeLater(new Sleeper("M", 500));

        assertThatExceptionOfType(TimeoutExpiredException.class)
                .isThrownBy(() -> qt.waitEmpty(600L))
                .withMessageContaining("timeout \"QueueTool_WaitQueueEmptyTimeout\" (3000 ms) exceeded after (");
    }

    private static class Sleeper implements Runnable {
        private final String name;
        private final long timeToSleep;

        Sleeper(String name, long timeToSleep) {
            this.timeToSleep = timeToSleep;
            this.name = name;
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                Thread.sleep(timeToSleep);
                success = true;
            } catch (InterruptedException e) {
                // don't care
            } finally {
                if (success) {
                    // good for you
                }
            }
        }
    }
}
