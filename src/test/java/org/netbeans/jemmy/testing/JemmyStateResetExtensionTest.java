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
 * Inc., 51 Franklin St, Fifth Floor, Boston, CA 94105 USA.
 */
package org.netbeans.jemmy.testing;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.netbeans.jemmy.NoBlockingActions;
import org.netbeans.jemmy.RunnableRunner;

@Isolated
class JemmyStateResetExtensionTest {

    /** Safety net so a broken test fails on the assertion instead of wedging the suite. */
    private static final long LATCH_WAIT_TIME = 30_000L;

    @Test
    void parksAtTheUsableScreenCornerFurthestFromTestWindows() {
        Point parkingPoint =
                JemmyStateResetExtension.pointerParkingPoint(new Rectangle(0, 0, 100, 80), new Point(10, 20));

        assertThat(parkingPoint).isEqualTo(new Point(99, 79));
    }

    @Test
    void supportsScreensWithNegativeCoordinates() {
        Point parkingPoint =
                JemmyStateResetExtension.pointerParkingPoint(new Rectangle(-100, -50, 100, 50), new Point(-90, -40));

        assertThat(parkingPoint).isEqualTo(new Point(-1, -1));
    }

    @Test
    void passesWhenNoBlockingActionsFinishWithinTheGracePeriod() throws InterruptedException {
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME)).isTrue();
        CountDownLatch ran = new CountDownLatch(1);
        RunnableRunner.on(ran::countDown).runLater();

        assertThat(JemmyStateResetExtension.finishNoBlockingActions(LATCH_WAIT_TIME, LATCH_WAIT_TIME, "then"))
                .isNull();
        assertThat(ran.getCount()).isZero();
    }

    @Test
    void cancelsAndReportsNoBlockingActionsThatOutliveTheGracePeriod() throws InterruptedException {
        assertThat(NoBlockingActions.awaitCompletion(LATCH_WAIT_TIME)).isTrue();
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch neverReleased = new CountDownLatch(1);
        RunnableRunner.on(() -> {
                    started.countDown();
                    try {
                        neverReleased.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                })
                .runLater();
        assertThat(started.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS)).isTrue();

        AssertionError failure = JemmyStateResetExtension.finishNoBlockingActions(
                50L, LATCH_WAIT_TIME, "50 ms after the test method returned");

        assertThat(failure)
                .isNotNull()
                .hasMessageStartingWith("1 no-blocking action was still unfinished 50 ms after the test method"
                        + " returned, so it was cancelled.")
                .hasMessageNotContaining("did not end");
        assertThat(failure.getSuppressed()).singleElement().satisfies(submission -> assertThat(
                        submission.getStackTrace())
                .anySatisfy(frame -> assertThat(frame.getMethodName())
                        .isEqualTo("cancelsAndReportsNoBlockingActionsThatOutliveTheGracePeriod")));
        assertThat(NoBlockingActions.awaitCompletion(0L))
                .as("the cancelled action no longer holds the action thread")
                .isTrue();
    }

    @Test
    void acceptsAResponsiveEventDispatchThread() throws InterruptedException {
        assertThat(JemmyStateResetExtension.edtProblem(LATCH_WAIT_TIME)).isNull();
    }

    @Test
    void reportsABlockedEventDispatchThreadWithItsStack() throws Exception {
        CountDownLatch blocking = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        EventQueue.invokeLater(() -> {
            blocking.countDown();
            try {
                release.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        try {
            assertThat(blocking.await(LATCH_WAIT_TIME, TimeUnit.MILLISECONDS)).isTrue();

            IllegalStateException problem = JemmyStateResetExtension.edtProblem(50L);

            assertThat(problem)
                    .isNotNull()
                    .hasMessageStartingWith("The event dispatch thread did not respond within 50 ms");
            assertThat(problem.getSuppressed()).anySatisfy(stack -> {
                assertThat(stack).hasMessageStartingWith("AWT-EventQueue");
                assertThat(stack.getStackTrace()).anySatisfy(frame -> assertThat(frame.getMethodName())
                        .contains("reportsABlockedEventDispatchThreadWithItsStack"));
            });
        } finally {
            release.countDown();
            EventQueue.invokeAndWait(() -> {});
        }
    }

    @Test
    void acceptsAnInstalledLookAndFeel() {
        assertThat(JemmyStateResetExtension.lookAndFeelProblem(new MetalLookAndFeel(), null)).isNull();
    }

    @Test
    void reportsALookAndFeelThatFailedToLoad() {
        Error loadError = new Error("Cannot load com.example.MissingLookAndFeel");

        assertThat(JemmyStateResetExtension.lookAndFeelProblem(null, loadError))
                .isNotNull()
                .hasMessageStartingWith("Swing has no usable look and feel (swing.defaultlaf=")
                .hasCause(loadError);
        assertThat(JemmyStateResetExtension.lookAndFeelProblem(null, null))
                .as("later callers get no error, only a missing look and feel")
                .isNotNull()
                .hasNoCause();
    }
}
