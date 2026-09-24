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

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.NoBlockingActions;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Restores the JVM to a just-started condition around every test class, standing in for the
 * one-JVM-per-class isolation the UI suite used to buy with Gradle's {@code forkEvery = 1}.
 *
 * <p>Before each class it first checks that UI tests can run at all, and fails the class with
 * the reason when they cannot: AWT is headless, Swing has no usable look and feel, a {@link
 * Robot} cannot be created, or the event dispatch thread does not respond. It fails rather than
 * skips, so a machine that cannot run the suite never reports it as passing. None of these
 * recover within a JVM, so the first failure is remembered and every later class fails with it
 * at once.
 *
 * <p>It then resets process-wide state before and after each class (before defensively, in case
 * the previous class died without its callbacks), in an order that mirrors a fresh JVM:
 * <ol>
 * <li>cancel no-blocking actions still queued or running on the Jemmy action thread</li>
 * <li>release a stray left mouse button, so a test that failed mid-drag cannot turn every later
 * click into a drag (right button and keyboard are left alone: a synthetic release without a
 * preceding press can pop platform menus)</li>
 * <li>park the physical pointer away from test windows and wait for its mouse-exit event, so
 * Swing's tooltip manager cannot retain the previous component as the hover target</li>
 * <li>dispose every window still showing, so stale windows cannot satisfy the next class's
 * lookups</li>
 * <li>restore the look and feel captured at suite start - before the driver reset, because the
 * default driver registry is chosen partly by look and feel</li>
 * <li>clear Swing's global tooltip state and apply short tooltip delays for the next class</li>
 * <li>{@link JemmyContext#resetAllState()}: window jobs, repaint manager, dispatching model,
 * drivers, queue installation, timeouts, event listeners</li>
 * <li>wait for the event queue to settle, best effort</li>
 * <li>forget the events observed while settling, so the next class starts with no last-event
 * memory</li>
 * </ol>
 *
 * <p>Before each test method, after its {@code @BeforeEach} methods have shown the test's windows,
 * the extension waits briefly for the focus that showing the last of them requested (see {@link
 * ShownWindowFocus}), so the test starts from settled focus as it would on Windows.
 *
 * <p>After each test method, before its {@code @AfterEach} methods tear the UI down, the extension
 * also gives the test's no-blocking actions ({@code pushNoBlock}, {@code pushMenuNoBlock} and the
 * like) a short grace period to finish. All of them share the single Jemmy action thread, so one
 * that outlives its test holds up every later action and makes unrelated tests time out. Any
 * still unfinished after the grace period are cancelled, and the test that left them fails with
 * the place each was submitted.
 *
 * <p>Every class in the UI suite registers this explicitly with
 * {@code @ExtendWith(JemmyStateResetExtension.class)}; consumer test classes should do the same.
 */
public final class JemmyStateResetExtension
        implements BeforeAllCallback, AfterAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Logger logger = LoggerFactory.getLogger(JemmyStateResetExtension.class);

    /** How long a test's no-blocking actions may keep running after the test method returns. */
    private static final long NO_BLOCKING_ACTION_GRACE_MS = 5_000L;

    /** How long a cancelled no-blocking action gets to notice its interrupt and end. */
    private static final long CANCELLED_ACTION_EXIT_MS = 5_000L;

    /** How long a test waits for the focus that showing its last window requested. */
    private static final long SHOWN_WINDOW_FOCUS_TIMEOUT_MS = 2_000L;

    /** How long the event dispatch thread may take to run a trivial task before a class starts. */
    private static final long EDT_RESPONSE_TIMEOUT_MS = 10_000L;

    /** The first reason found that UI tests cannot run in this JVM; it is never cleared. */
    private static @Nullable IllegalStateException unusableDesktop;

    /** Whether the checks that cannot change during a JVM's life have passed. */
    private static boolean desktopVerified;

    /** The look and feel class active before any test class ran; first callback wins. */
    private static final AtomicReference<@Nullable String> pristineLookAndFeel = new AtomicReference<>();

    private static @Nullable Robot robot;
    private static boolean pristineToolTipStateCaptured;
    private static int pristineToolTipInitialDelay;
    private static int pristineToolTipReshowDelay;
    private static int pristineToolTipDismissDelay;
    private static boolean pristineToolTipsEnabled;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (isNestedClass(context)) {
            return;
        }

        requireUsableDesktop();
        ShownWindowFocus.install();
        capturePristineToolTipState();
        resetEverything();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        // the reset needs the event dispatch thread and the robot that the failed check found
        // unusable; beforeAll has already failed this class with the reason
        if (isNestedClass(context) || unusableDesktop() != null) {
            return;
        }

        resetEverything();
        restoreToolTipState();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        if (unusableDesktop() == null) {
            ShownWindowFocus.awaitFocus(SHOWN_WINDOW_FOCUS_TIMEOUT_MS);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        AssertionError leftover = finishNoBlockingActions(
                NO_BLOCKING_ACTION_GRACE_MS,
                CANCELLED_ACTION_EXIT_MS,
                NO_BLOCKING_ACTION_GRACE_MS + " ms after the test method returned");
        if (leftover != null) {
            throw leftover;
        }
    }

    /**
     * Waits up to {@code graceMillis} for every no-blocking action to finish, then cancels the
     * rest and waits up to {@code exitMillis} for a cancelled running action to end.
     *
     * @param when completes "N actions were still unfinished ..." in the failure message
     * @return {@code null} when none had to be cancelled, otherwise a failure with one suppressed
     *     throwable per cancelled action showing where it was submitted
     */
    static @Nullable AssertionError finishNoBlockingActions(long graceMillis, long exitMillis, String when)
            throws InterruptedException {
        if (NoBlockingActions.awaitCompletion(graceMillis)) {
            return null;
        }

        List<Throwable> submissions = NoBlockingActions.cancelAll();
        if (submissions.isEmpty()) {
            // the last one finished between the timed-out wait and the cancellation
            return null;
        }

        boolean ended = NoBlockingActions.awaitCompletion(exitMillis);
        StringBuilder message = new StringBuilder()
                .append(submissions.size())
                .append(submissions.size() == 1 ? " no-blocking action was" : " no-blocking actions were")
                .append(" still unfinished ")
                .append(when)
                .append(", so ")
                .append(submissions.size() == 1 ? "it was" : "they were")
                .append(" cancelled. Wait for each ...NoBlock call to take effect before the test ends;")
                .append(" the suppressed exceptions show where each action was submitted.");
        if (!ended) {
            message.append(" The running action did not end within ")
                    .append(exitMillis)
                    .append(" ms of being interrupted and still holds the Jemmy action thread.");
        }
        AssertionError failure = new AssertionError(message.toString());
        for (Throwable submission : submissions) {
            failure.addSuppressed(submission);
        }
        return failure;
    }

    private static synchronized @Nullable IllegalStateException unusableDesktop() {
        return unusableDesktop;
    }

    private static synchronized void requireUsableDesktop() throws Exception {
        if (unusableDesktop == null) {
            unusableDesktop = findDesktopProblem(!desktopVerified);
            desktopVerified = unusableDesktop == null;
        }
        if (unusableDesktop != null) {
            // a fresh exception per class: JUnit attaches later failures to the one it reports
            IllegalStateException failure = new IllegalStateException(unusableDesktop.getMessage());
            failure.initCause(unusableDesktop.getCause());
            for (Throwable detail : unusableDesktop.getSuppressed()) {
                failure.addSuppressed(detail);
            }
            throw failure;
        }
    }

    /**
     * @param fullCheck also run the checks whose outcome cannot change during the JVM's life
     */
    private static @Nullable IllegalStateException findDesktopProblem(boolean fullCheck) throws Exception {
        if (fullCheck && GraphicsEnvironment.isHeadless()) {
            String headless = System.getProperty("java.awt.headless");
            return new IllegalStateException("UI tests need a display, but AWT is headless ("
                    + (headless == null ? "no display was found" : "java.awt.headless=" + headless)
                    + "). On Linux, run Gradle under xvfb-run.");
        }

        // before anything else touches the event dispatch thread, which would otherwise hang
        IllegalStateException edtProblem = edtProblem(EDT_RESPONSE_TIMEOUT_MS);
        if (edtProblem != null || !fullCheck) {
            return edtProblem;
        }

        AtomicReference<javax.swing.@Nullable LookAndFeel> lookAndFeel = new AtomicReference<>();
        AtomicReference<@Nullable Error> loadError = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> {
            try {
                lookAndFeel.set(UIManager.getLookAndFeel());
            } catch (VirtualMachineError e) {
                throw e;
            } catch (Error e) {
                // UIManager reports a default look and feel it cannot load this way, once
                loadError.set(e);
            }
        });
        javax.swing.LookAndFeel installed = lookAndFeel.get();
        IllegalStateException lookAndFeelProblem = lookAndFeelProblem(installed, loadError.get());
        if (lookAndFeelProblem != null || installed == null) {
            return lookAndFeelProblem;
        }
        // the look and feel active before any test class ran, restored between classes
        pristineLookAndFeel.compareAndSet(null, installed.getClass().getName());

        try {
            robot();
        } catch (AWTException | SecurityException e) {
            return new IllegalStateException(
                    "UI tests need a java.awt.Robot to drive the mouse and keyboard, but one cannot be created", e);
        }
        return null;
    }

    /**
     * Runs a trivial task on the event dispatch thread. When it does not run in time, a previous
     * test has probably left the thread blocked, and nothing that needs it can run in this JVM.
     *
     * @return {@code null} when the thread responded, otherwise the failure with the thread's
     *     stack attached as a suppressed exception
     */
    static @Nullable IllegalStateException edtProblem(long timeoutMillis) throws InterruptedException {
        CountDownLatch responded = new CountDownLatch(1);
        EventQueue.invokeLater(responded::countDown);
        if (responded.await(timeoutMillis, TimeUnit.MILLISECONDS)) {
            return null;
        }

        IllegalStateException problem = new IllegalStateException("The event dispatch thread did not respond"
                + " within " + timeoutMillis + " ms, so no UI test can run in this JVM. A previous test probably"
                + " left it blocked; its stack is attached.");
        for (Map.Entry<Thread, StackTraceElement[]> thread : Thread.getAllStackTraces().entrySet()) {
            if (thread.getKey().getName().startsWith("AWT-EventQueue")) {
                Throwable stack = new Throwable(thread.getKey().getName() + " [" + thread.getKey().getState() + "]");
                stack.setStackTrace(thread.getValue());
                problem.addSuppressed(stack);
            }
        }
        return problem;
    }

    /**
     * @param loadError what {@code UIManager.getLookAndFeel()} threw, if it threw
     * @return {@code null} when a look and feel is installed
     */
    static @Nullable IllegalStateException lookAndFeelProblem(
            javax.swing.@Nullable LookAndFeel lookAndFeel, @Nullable Error loadError) {
        if (lookAndFeel != null && loadError == null) {
            return null;
        }

        return new IllegalStateException("Swing has no usable look and feel (swing.defaultlaf="
                + System.getProperty("swing.defaultlaf") + "). A look and feel this platform cannot load,"
                + " such as WindowsLookAndFeel on Linux, breaks Swing for the whole JVM.", loadError);
    }

    /** A {@code @Nested} class runs inside its enclosing class; resetting there would sabotage it. */
    private static boolean isNestedClass(ExtensionContext context) {
        return context.getTestClass().map(testClass -> testClass.getEnclosingClass() != null).orElse(false);
    }

    private static void resetEverything() throws Exception {
        cancelLeftoverActions();
        releaseStrayMouseButton();
        parkPointerAwayFromTestWindows();
        TestWindows.disposeAll();
        restoreLookAndFeel();
        resetToolTipState();
        JemmyContext.resetAllState();
        settleEventQueue();
        // disposal events that dispatched while the queue settled must not be remembered as the
        // next class's "last event"
        EventTool.getInstance().clearLastEvents();
    }

    /**
     * Backstop for actions left by a class that does not register this extension: the per-test
     * check fails the test that leaves one, so none should remain between classes.
     */
    private static void cancelLeftoverActions() throws InterruptedException {
        AssertionError leftover = finishNoBlockingActions(0L, CANCELLED_ACTION_EXIT_MS, "between test classes");
        if (leftover != null) {
            // failing here would blame whichever class runs next, not the one that left them
            logger.warn("cancelled no-blocking actions left over between test classes", leftover);
        }
    }

    private static synchronized Robot robot() throws AWTException {
        Robot current = robot;
        if (current == null) {
            current = new Robot();
            robot = current;
        }
        return current;
    }

    private static void releaseStrayMouseButton() throws AWTException {
        robot().mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Generates a real mouse-exit before test windows are disposed. Disabling {@link
     * ToolTipManager} hides a current popup, but the manager has no public API to clear its
     * private hover component and timer state. Moving the OS pointer away while the old component
     * still exists supplies the normal Swing event that clears that state.
     */
    private static void parkPointerAwayFromTestWindows() throws AWTException {
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Point parkingPoint = pointerParkingPoint(screenBounds, TestWindows.baseLocation());
        Robot pointer = robot();
        pointer.mouseMove(parkingPoint.x, parkingPoint.y);
        pointer.waitForIdle();
    }

    /**
     * Chooses the usable-screen corner furthest from the test-window base location. Test windows
     * are placed at or cascaded from that base, so the opposite corner is a stable parking spot
     * even when the test location is overridden.
     */
    static Point pointerParkingPoint(Rectangle screenBounds, Point testWindowBase) {
        int maxX = screenBounds.x + Math.max(0, screenBounds.width - 1);
        int maxY = screenBounds.y + Math.max(0, screenBounds.height - 1);
        Point[] candidates = {
            new Point(screenBounds.x, screenBounds.y),
            new Point(screenBounds.x, maxY),
            new Point(maxX, screenBounds.y),
            new Point(maxX, maxY)
        };

        Point result = candidates[0];
        long greatestDistanceSquared = distanceSquared(result, testWindowBase);
        for (int i = 1; i < candidates.length; i++) {
            long distanceSquared = distanceSquared(candidates[i], testWindowBase);
            if (distanceSquared > greatestDistanceSquared) {
                result = candidates[i];
                greatestDistanceSquared = distanceSquared;
            }
        }

        return result;
    }

    private static long distanceSquared(Point first, Point second) {
        long deltaX = (long) first.x - second.x;
        long deltaY = (long) first.y - second.y;
        return deltaX * deltaX + deltaY * deltaY;
    }

    private static void restoreLookAndFeel() throws Exception {
        String pristine = pristineLookAndFeel.get();
        if (pristine == null) {
            return;
        }

        EventQueue.invokeAndWait(() -> {
            if (UIManager.getLookAndFeel().getClass().getName().equals(pristine)) {
                return;
            }

            try {
                UIManager.setLookAndFeel(pristine);
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                throw new IllegalStateException("could not restore look and feel " + pristine, e);
            }
        });
    }

    private static void capturePristineToolTipState() throws Exception {
        if (pristineToolTipStateCaptured) {
            return;
        }

        EventQueue.invokeAndWait(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();
            pristineToolTipInitialDelay = manager.getInitialDelay();
            pristineToolTipReshowDelay = manager.getReshowDelay();
            pristineToolTipDismissDelay = manager.getDismissDelay();
            pristineToolTipsEnabled = manager.isEnabled();
            pristineToolTipStateCaptured = true;
        });
    }

    private static void resetToolTipState() throws Exception {
        EventQueue.invokeAndWait(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();

            // Toggling enabled clears any pending/current tooltip and leaves Swing's singleton in
            // a known state. The test suite deliberately uses short delays because tooltip tests
            // exercise the popup, not the platform's human-facing hover delay.
            manager.setEnabled(false);
            manager.setInitialDelay(50);
            manager.setReshowDelay(0);
            manager.setDismissDelay(pristineToolTipDismissDelay);
            manager.setEnabled(true);
        });
    }

    private static void restoreToolTipState() throws Exception {
        EventQueue.invokeAndWait(() -> {
            ToolTipManager manager = ToolTipManager.sharedInstance();
            manager.setEnabled(false);
            manager.setInitialDelay(pristineToolTipInitialDelay);
            manager.setReshowDelay(pristineToolTipReshowDelay);
            manager.setDismissDelay(pristineToolTipDismissDelay);
            manager.setEnabled(pristineToolTipsEnabled);
        });
    }

    private static void settleEventQueue() {
        try {
            QueueTool.getInstance().waitEmpty();
        } catch (TimeoutExpiredException e) {
            // isolation between classes should not itself fail a class; the next class's own
            // waits will surface a genuinely wedged queue
            logger.warn("event queue did not settle between test classes", e);
        }
    }
}
