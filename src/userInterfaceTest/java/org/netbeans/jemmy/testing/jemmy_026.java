package org.netbeans.jemmy.testing;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.netbeans.jemmy.*;

import javax.swing.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(qt::waitEmpty)
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

        assertThatExceptionOfType(TimeoutExpiredException.class).isThrownBy(() -> qt.waitEmpty(600L))
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
