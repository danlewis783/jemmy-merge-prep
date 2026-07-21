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
package org.netbeans.jemmy.drivers.input;

import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JWindow;
import org.jetbrains.annotations.Nullable;
import org.netbeans.jemmy.JemmyException;
import org.netbeans.jemmy.QueueTool;
import org.netbeans.jemmy.util.Display;

/**
 * Maps logical screen coordinates to the coordinate space {@link Robot#mouseMove} actually moves
 * in. On Java 8 under Windows display scaling the two spaces differ by the scale factor:
 * component locations are reported in virtualized (logical) pixels while the robot moves the
 * pointer in physical pixels, so an uncorrected move lands short of its target by the scale
 * factor.
 *
 * <p>Rather than assume any particular scaling model, the mapping is measured once per session:
 * a non-focusable, always-on-top window covering the primary screen observes where robot moves
 * to known request points actually land (via the mouse events Swing delivers), a per-axis linear
 * transform is fitted from the observations, and the fit is then verified by moving to computed
 * request points and checking the observed landing positions match the intended targets. The
 * verification also selects a sub-pixel aim offset per axis, so that integer logical targets are
 * hit as exactly as the request grid allows regardless of whether the platform truncates or
 * rounds when converting the pointer position back to logical pixels. Calibration fails with a
 * {@link JemmyException} rather than letting robot input proceed with a mapping that has been
 * shown not to land where it aims. When first triggered on the event dispatch thread, the
 * calibration runs on a worker thread while the EDT keeps dispatching events in a
 * {@link SecondaryLoop}.
 *
 * <p>On some scaled-display configurations (observed on a docked laptop driving a single 4K
 * display at 125%) a single cursor move can land off target in a history-dependent way — the
 * landing varies with where the cursor came from — while an immediate repeat of the same move
 * lands consistently. Every probe therefore issues its move twice (the repeat is a no-op when
 * the first move already landed), and the fit uses the median of pairwise slopes across several
 * probes, so an individual poisoned reading cannot corrupt the model.
 *
 * <p>On unscaled displays {@link Display#isScaled()} short-circuits all of this to the identity
 * mapping, so calibration (and its brief full-screen window flash) only ever happens on machines
 * where uncorrected robot input would miss. The primary screen is the calibrated space; tests
 * driving robot input on a secondary monitor with a different scale are out of scope.
 */
public final class RobotCalibration {

    /**
     * Sub-pixel aim offset applied when inverting the fit; a quarter pixel is a neutral
     * compromise between platforms that truncate and platforms that round when reporting the
     * pointer position. The mapping only seeds the closed-loop landing in
     * {@code RobotDriver.landMouse}, so sub-pixel tuning beyond this is not worth probe time.
     */
    private static final double AIM = 0.25;

    /** Verification target positions as fractions of the primary screen size, chosen to stay on
     * the primary screen even when the request space is 2.5x finer than the logical space. */
    private static final double[] TARGET_FRACTIONS = {0.22, 0.30, 0.38};

    /** Fit probe positions as fractions of the primary screen size. Five probes give the median
     * slope estimate enough pairs to outvote an individual poisoned reading. */
    private static final double[] FIT_FRACTIONS = {0.10, 0.19, 0.28, 0.37, 0.45};

    private static final long PROBE_TIMEOUT_MS = 5_000L;

    /** How long the observed-event queue must stay quiet before a probe reading is final, so a
     * reading is never taken while a later position report is still in flight. */
    private static final long QUIET_PERIOD_MS = 50L;

    private static final int ROBOT_AUTO_DELAY_MS = 10;

    private static boolean initialized;
    private static @Nullable Mapping mapping;

    private RobotCalibration() {}

    /**
     * Maps a logical screen point to the point to pass to {@link Robot#mouseMove}. The first call
     * on a scaled display runs the calibration; on unscaled displays this is the identity.
     */
    public static synchronized Point map(int x, int y) {
        if (!initialized) {
            mapping = Display.isScaled() ? calibrateOnAnyThread() : null;
            initialized = true;
        }

        return (mapping == null) ? new Point(x, y) : new Point(mapping.mapX(x), mapping.mapY(y));
    }

    /**
     * True when a non-identity mapping is in effect (calibration ran on a scaled display).
     * Movers should then verify each move's landing against the actual pointer position and
     * correct the residual (see {@code RobotDriver.landMouse}): on such displays the conversion
     * of an injected move is history- and timing-dependent, so the mapped request is only a
     * seed, not a guarantee.
     */
    public static synchronized boolean isActive() {
        return mapping != null;
    }

    /**
     * Runs the calibration from whatever thread first needed the mapping. Robot input is
     * frequently dispatched on the event dispatch thread, where the calibration cannot simply
     * block (the mouse events it waits for are dispatched by that very thread); there it runs on
     * a worker thread while the EDT keeps pumping events in a {@link SecondaryLoop}.
     */
    private static Mapping calibrateOnAnyThread() {
        if (!EventQueue.isDispatchThread()) {
            return calibrate();
        }

        SecondaryLoop loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        AtomicReference<Object> outcome = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            try {
                outcome.set(calibrate());
            } catch (RuntimeException e) {
                outcome.set(e);
            } finally {
                // exit() before the EDT reaches enter() returns false and would strand the EDT
                // in the loop forever; retry until the entered loop is really exited
                try {
                    for (int i = 0; (i < 600) && !loop.exit(); i++) {
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "jemmy-robot-calibration");
        worker.setDaemon(true);
        worker.start();
        if (!loop.enter()) {
            throw new JemmyException(
                    "could not enter a secondary event loop to run robot calibration off the event dispatch thread");
        }

        Object result = outcome.get();
        if (result instanceof RuntimeException) {
            throw (RuntimeException) result;
        }
        if (result == null) {
            throw new JemmyException("robot calibration worker terminated without a result");
        }

        return (Mapping) result;
    }

    /** Measures and verifies the request-space mapping for the primary screen. */
    static Mapping calibrate() {
        if (EventQueue.isDispatchThread()) {
            throw new IllegalStateException(
                    "robot calibration blocks waiting for mouse events and cannot run on the event dispatch thread");
        }

        QueueTool queueTool = QueueTool.getInstance();
        Robot robot = queueTool.callOnQueue(() -> {
            Robot instance = new Robot();
            instance.setAutoDelay(ROBOT_AUTO_DELAY_MS);
            return instance;
        });
        Rectangle bounds = queueTool.callOnQueue(() -> GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds());

        CalibrationScreen screen = CalibrationScreen.show(bounds, queueTool);
        try {
            Point park = at(bounds, 0.05);
            int probeCount = FIT_FRACTIONS.length;
            int[] requestsX = new int[probeCount];
            int[] requestsY = new int[probeCount];
            int[] observedX = new int[probeCount];
            int[] observedY = new int[probeCount];
            for (int i = 0; i < probeCount; i++) {
                Point request = at(bounds, FIT_FRACTIONS[i]);
                Point observed = probe(robot, screen, park, request);
                requestsX[i] = request.x;
                requestsY[i] = request.y;
                observedX[i] = observed.x;
                observedY[i] = observed.y;
            }

            Linear x = Linear.fit(requestsX, observedX);
            Linear y = Linear.fit(requestsY, observedY);
            return verify(robot, screen, bounds, park, x, y);
        } finally {
            screen.dispose(queueTool);
            // the caller's robot input follows immediately; if the calibration window's native
            // teardown has not finished, that input lands on the dying window instead of the
            // caller's target — wait until it is really gone
            robot.waitForIdle();
            try {
                Thread.sleep(150L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Moves through the fitted mapping to a few known logical targets and checks where the
     * moves land. The mapping only seeds the closed-loop landing in
     * {@code RobotDriver.landMouse}, which corrects the residual error per move, so seed
     * quality within a few pixels is sufficient; beyond that the fit did not capture the
     * display's behavior at all and robot input would wander, so calibration fails instead.
     */
    private static Mapping verify(
            Robot robot, CalibrationScreen screen, Rectangle bounds, Point park, Linear x, Linear y) {
        StringBuilder diagnostics = new StringBuilder();
        int maxError = 0;
        for (int i = 0; i < TARGET_FRACTIONS.length; i++) {
            // the +i varies the fractional phase so the targets exercise different points of
            // the request grid
            Point target = new Point(
                    bounds.x + (int) (bounds.width * TARGET_FRACTIONS[i]) + i,
                    bounds.y + (int) (bounds.height * TARGET_FRACTIONS[i]) + i);
            Point request = new Point(x.requestFor(target.x, AIM), y.requestFor(target.y, AIM));
            Point observed = probe(robot, screen, park, request);
            maxError = Math.max(maxError, Math.abs(observed.x - target.x));
            maxError = Math.max(maxError, Math.abs(observed.y - target.y));
            diagnostics
                    .append("target=").append(target.x).append(',').append(target.y)
                    .append(" observed=").append(observed.x).append(',').append(observed.y)
                    .append(System.lineSeparator());
        }

        if (maxError > 5) {
            throw new JemmyException("robot calibration could not find a mapping that lands robot moves near their"
                    + " targets (fitted x: " + x + ", y: " + y + ")" + System.lineSeparator() + diagnostics);
        }

        return new Mapping(x, AIM, y, AIM);
    }

    /**
     * Moves to the park point, then to the request point twice, and returns where the pointer
     * finally landed. The park hop guarantees the request move actually changes the pointer
     * position, so a mouse event is always generated. The repeated request move counters
     * scaled-display configurations where a single move's landing depends on where the cursor
     * came from: the repeat lands consistently, and generates no further event when the first
     * move already landed exactly.
     */
    private static Point probe(Robot robot, CalibrationScreen screen, Point park, Point request) {
        robot.mouseMove(park.x, park.y);
        robot.waitForIdle();
        screen.reset();
        robot.mouseMove(request.x, request.y);
        robot.waitForIdle();
        robot.mouseMove(request.x, request.y);
        robot.waitForIdle();
        Point observed = screen.awaitPointer(PROBE_TIMEOUT_MS);
        if (observed == null) {
            throw new JemmyException("no mouse event reached the calibration window within " + PROBE_TIMEOUT_MS
                    + " ms for a robot move to " + request.x + "," + request.y
                    + " (primary screen " + screen.bounds() + ")");
        }

        return observed;
    }

    private static Point at(Rectangle bounds, double fraction) {
        return new Point(bounds.x + (int) (bounds.width * fraction), bounds.y + (int) (bounds.height * fraction));
    }

    /** Per-axis calibrated mapping from logical targets to robot request coordinates. */
    static final class Mapping {
        private final Linear x;
        private final double aimX;
        private final Linear y;
        private final double aimY;

        Mapping(Linear x, double aimX, Linear y, double aimY) {
            this.x = x;
            this.aimX = aimX;
            this.y = y;
            this.aimY = aimY;
        }

        int mapX(int logicalX) {
            return x.requestFor(logicalX, aimX);
        }

        int mapY(int logicalY) {
            return y.requestFor(logicalY, aimY);
        }
    }

    /** One axis of the fitted model: {@code observed = scale * request + offset}. */
    static final class Linear {
        final double scale;
        final double offset;

        private Linear(double scale, double offset) {
            this.scale = scale;
            this.offset = offset;
        }

        /**
         * Theil-Sen fit: the median of all pairwise slopes, with the median residual as the
         * offset. The medians make the fit immune to a minority of poisoned probe readings,
         * which some scaled-display configurations produce (see the class comment).
         */
        static Linear fit(int[] requests, int[] observed) {
            int count = requests.length;
            double[] slopes = new double[count * (count - 1) / 2];
            int pair = 0;
            for (int i = 0; i < count; i++) {
                for (int j = i + 1; j < count; j++) {
                    slopes[pair++] = (double) (observed[j] - observed[i]) / (requests[j] - requests[i]);
                }
            }

            double scale = median(slopes);
            if ((Math.abs(observed[count - 1] - observed[0]) < 10) || (scale <= 0.1)) {
                throw new JemmyException("mouse pointer did not track the calibration probes (requests "
                        + Arrays.toString(requests) + " were observed at " + Arrays.toString(observed) + ")");
            }

            double[] residuals = new double[count];
            for (int i = 0; i < count; i++) {
                residuals[i] = observed[i] - scale * requests[i];
            }

            return new Linear(scale, median(residuals));
        }

        private static double median(double[] values) {
            double[] sorted = values.clone();
            Arrays.sort(sorted);
            int middle = sorted.length / 2;
            return ((sorted.length % 2) == 1) ? sorted[middle] : ((sorted[middle - 1] + sorted[middle]) / 2.0);
        }

        /** The request coordinate whose observed landing is the given logical target. */
        int requestFor(int target, double aim) {
            return (int) Math.round((target + aim - offset) / scale);
        }

        @Override
        public String toString() {
            return "observed=" + scale + "*request+" + offset;
        }
    }

    /**
     * A non-focusable window covering the primary screen that records where mouse events land.
     * Non-focusable so that showing it mid-test cannot steal focus (which would, for example,
     * dismiss an open popup menu).
     */
    private static final class CalibrationScreen {
        private final JWindow window;
        private final Rectangle bounds;
        private final LinkedBlockingQueue<Point> observed = new LinkedBlockingQueue<>();

        private CalibrationScreen(JWindow window, Rectangle bounds) {
            this.window = window;
            this.bounds = bounds;
        }

        static CalibrationScreen show(Rectangle bounds, QueueTool queueTool) {
            return queueTool.callOnQueue(() -> {
                JWindow window = new JWindow();
                window.setAlwaysOnTop(true);
                window.setFocusableWindowState(false);
                window.setBounds(bounds);
                CalibrationScreen screen = new CalibrationScreen(window, bounds);
                MouseAdapter recorder = new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        screen.observed.add(e.getLocationOnScreen());
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        screen.observed.add(e.getLocationOnScreen());
                    }
                };
                window.getContentPane().addMouseListener(recorder);
                window.getContentPane().addMouseMotionListener(recorder);
                window.setVisible(true);
                window.toFront();
                return screen;
            });
        }

        Rectangle bounds() {
            return bounds;
        }

        void reset() {
            observed.clear();
        }

        /** The last observed landing point, or null if none arrives within the timeout. */
        @Nullable Point awaitPointer(long timeoutMs) {
            try {
                Point point = observed.poll(timeoutMs, TimeUnit.MILLISECONDS);
                if (point == null) {
                    return null;
                }

                // keep draining until the queue stays quiet for a beat, so the reading is never
                // taken while a later (more final) position report is still in flight; a
                // stationary pointer stops reporting, so this terminates
                Point later;
                while ((later = observed.poll(QUIET_PERIOD_MS, TimeUnit.MILLISECONDS)) != null) {
                    point = later;
                }

                return point;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new JemmyException("interrupted while waiting for a calibration mouse event", e);
            }
        }

        void dispose(QueueTool queueTool) {
            queueTool.runOnQueue(() -> {
                window.setVisible(false);
                window.dispose();
            });
        }
    }
}
