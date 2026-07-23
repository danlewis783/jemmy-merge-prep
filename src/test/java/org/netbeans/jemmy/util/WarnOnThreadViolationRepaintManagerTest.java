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
package org.netbeans.jemmy.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.netbeans.jemmy.testing.OnQueue.onQueue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import javax.swing.JLabel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * This test runs on the JUnit thread, which is never the event dispatch thread, so calling the
 * manager's entry points directly IS an EDT violation from the checker's point of view.
 */
class WarnOnThreadViolationRepaintManagerTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void attachAppender() {
        logger = (Logger) LoggerFactory.getLogger(WarnOnThreadViolationRepaintManager.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void detachAppender() {
        logger.detachAppender(appender);
    }

    @Test
    void offEdtDirtyRegionIsLoggedWithTheComponent() {
        JLabel label = onQueue(JLabel::new);
        WarnOnThreadViolationRepaintManager manager = new WarnOnThreadViolationRepaintManager();

        manager.addDirtyRegion(label, 0, 0, 10, 10);

        assertThat(appender.list)
                .filteredOn(event -> event.getLevel() == Level.WARN)
                .anySatisfy(event -> assertThat(event.getFormattedMessage())
                        .contains("EDT violation detected on javax.swing.JLabel"));
    }
}
