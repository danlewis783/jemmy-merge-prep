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
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.JemmyContext;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Restores the JVM to a just-started condition around every test class, standing in for the
 * one-JVM-per-class isolation the UI suite used to buy with Gradle's {@code forkEvery = 1}. Runs
 * before and after each class (before defensively, in case the previous class died without its
 * callbacks), in an order that mirrors a fresh JVM:
 * <ol>
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
 * Every class in the UI suite registers this explicitly with
 * {@code @ExtendWith(JemmyStateResetExtension.class)}; consumer test classes should do the same.
 */
public final class JemmyStateResetExtension implements BeforeAllCallback, AfterAllCallback {
    private static final Logger logger = LoggerFactory.getLogger(JemmyStateResetExtension.class);

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

        capturePristineLookAndFeel();
        capturePristineToolTipState();
        resetEverything();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (isNestedClass(context)) {
            return;
        }

        resetEverything();
        restoreToolTipState();
    }

    /** A {@code @Nested} class runs inside its enclosing class; resetting there would sabotage it. */
    private static boolean isNestedClass(ExtensionContext context) {
        return context.getTestClass().map(testClass -> testClass.getEnclosingClass() != null).orElse(false);
    }

    private static void capturePristineLookAndFeel() throws Exception {
        if (pristineLookAndFeel.get() != null) {
            return;
        }

        AtomicReference<String> current = new AtomicReference<>();
        EventQueue.invokeAndWait(() -> current.set(UIManager.getLookAndFeel().getClass().getName()));
        pristineLookAndFeel.compareAndSet(null, current.get());
    }

    private static void resetEverything() throws Exception {
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

    private static synchronized void releaseStrayMouseButton() throws AWTException {
        if (robot == null) {
            robot = new Robot();
        }

        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Generates a real mouse-exit before test windows are disposed. Disabling {@link
     * ToolTipManager} hides a current popup, but the manager has no public API to clear its
     * private hover component and timer state. Moving the OS pointer away while the old component
     * still exists supplies the normal Swing event that clears that state.
     */
    private static synchronized void parkPointerAwayFromTestWindows() throws AWTException {
        if (robot == null) {
            robot = new Robot();
        }

        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Point parkingPoint = pointerParkingPoint(screenBounds, TestWindows.baseLocation());
        robot.mouseMove(parkingPoint.x, parkingPoint.y);
        robot.waitForIdle();
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
