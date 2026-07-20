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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
 * hit exactly regardless of whether the platform truncates or rounds when converting the pointer
 * position back to logical pixels. Calibration fails with a {@link JemmyException} rather than
 * letting robot input proceed with a mapping that has been shown not to land where it aims.
 *
 * <p>On unscaled displays {@link Display#isScaled()} short-circuits all of this to the identity
 * mapping, so calibration (and its brief full-screen window flash) only ever happens on machines
 * where uncorrected robot input would miss. The primary screen is the calibrated space; tests
 * driving robot input on a secondary monitor with a different scale are out of scope.
 */
public final class RobotCalibration {

    /**
     * Sub-pixel aim offsets tried during verification, in preference order. 0.0 suits platforms
     * that round the pointer position to logical pixels (and the identity mapping); 0.5 centers
     * the landing inside the target pixel on platforms that truncate.
     */
    private static final double[] CANDIDATE_AIMS = {0.0, 0.5, 0.25};

    /** Verification target positions as fractions of the primary screen size, chosen to stay on
     * the primary screen even when the request space is 2.5x finer than the logical space. */
    private static final double[] TARGET_FRACTIONS = {0.20, 0.26, 0.32, 0.38};

    private static final long PROBE_TIMEOUT_MS = 5_000L;
    private static final int ROBOT_AUTO_DELAY_MS = 10;

    /** Scales at or below this still reach every logical pixel, so verification demands
     * exact landings; a coarser request grid cannot represent every target. */
    private static final double FINE_GRID_MAX_SCALE = 1.02;

    private static boolean initialized;
    private static @Nullable Mapping mapping;

    private RobotCalibration() {}

    /**
     * Maps a logical screen point to the point to pass to {@link Robot#mouseMove}. The first call
     * on a scaled display runs the calibration; on unscaled displays this is the identity.
     */
    public static synchronized Point map(int x, int y) {
        if (!initialized) {
            mapping = Display.isScaled() ? calibrate() : null;
            initialized = true;
        }

        return (mapping == null) ? new Point(x, y) : new Point(mapping.mapX(x), mapping.mapY(y));
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
            Point fitRequest1 = at(bounds, 0.10);
            Point fitRequest2 = at(bounds, 0.45);
            Point observed1 = probe(robot, screen, park, fitRequest1);
            Point observed2 = probe(robot, screen, park, fitRequest2);
            Linear x = Linear.fit(fitRequest1.x, observed1.x, fitRequest2.x, observed2.x);
            Linear y = Linear.fit(fitRequest1.y, observed1.y, fitRequest2.y, observed2.y);
            return verifyAndChooseAims(robot, screen, bounds, park, x, y);
        } finally {
            screen.dispose(queueTool);
        }
    }

    /**
     * Moves through the fitted mapping to known logical targets and checks where the moves land,
     * picking the sub-pixel aim offset per axis that lands exactly. Fails if no candidate does
     * (or, for a request grid coarser than the logical grid, if any landing is off by more than
     * one pixel).
     */
    private static Mapping verifyAndChooseAims(
            Robot robot, CalibrationScreen screen, Rectangle bounds, Point park, Linear x, Linear y) {
        int targetCount = TARGET_FRACTIONS.length;
        double bestAimX = 0.0;
        double bestAimY = 0.0;
        int bestExactX = -1;
        int bestExactY = -1;
        int bestMaxErrorX = Integer.MAX_VALUE;
        int bestMaxErrorY = Integer.MAX_VALUE;
        StringBuilder diagnostics = new StringBuilder();

        for (double aim : CANDIDATE_AIMS) {
            int exactX = 0;
            int exactY = 0;
            int maxErrorX = 0;
            int maxErrorY = 0;
            for (int i = 0; i < targetCount; i++) {
                // the +i varies the fractional phase so the targets exercise different points
                // of the request grid
                Point target = new Point(
                        bounds.x + (int) (bounds.width * TARGET_FRACTIONS[i]) + i,
                        bounds.y + (int) (bounds.height * TARGET_FRACTIONS[i]) + i);
                Point request = new Point(x.requestFor(target.x, aim), y.requestFor(target.y, aim));
                Point observed = probe(robot, screen, park, request);
                int errorX = Math.abs(observed.x - target.x);
                int errorY = Math.abs(observed.y - target.y);
                if (errorX == 0) {
                    exactX++;
                }
                if (errorY == 0) {
                    exactY++;
                }
                maxErrorX = Math.max(maxErrorX, errorX);
                maxErrorY = Math.max(maxErrorY, errorY);
                diagnostics
                        .append("aim=").append(aim)
                        .append(" target=").append(target.x).append(',').append(target.y)
                        .append(" observed=").append(observed.x).append(',').append(observed.y)
                        .append(System.lineSeparator());
            }

            if (exactX > bestExactX) {
                bestExactX = exactX;
                bestMaxErrorX = maxErrorX;
                bestAimX = aim;
            }
            if (exactY > bestExactY) {
                bestExactY = exactY;
                bestMaxErrorY = maxErrorY;
                bestAimY = aim;
            }
            if ((bestExactX == targetCount) && (bestExactY == targetCount)) {
                break;
            }
        }

        boolean xAcceptable =
                (x.scale <= FINE_GRID_MAX_SCALE) ? (bestExactX == targetCount) : (bestMaxErrorX <= 1);
        boolean yAcceptable =
                (y.scale <= FINE_GRID_MAX_SCALE) ? (bestExactY == targetCount) : (bestMaxErrorY <= 1);
        if (!xAcceptable || !yAcceptable) {
            throw new JemmyException("robot calibration could not find a mapping that lands robot moves on their"
                    + " targets (fitted x: " + x + ", y: " + y + ")" + System.lineSeparator() + diagnostics);
        }

        return new Mapping(x, bestAimX, y, bestAimY);
    }

    /**
     * Moves to the park point, then to the request point, and returns where the second move
     * landed. The park hop guarantees the request move actually changes the pointer position, so
     * a mouse event is always generated.
     */
    private static Point probe(Robot robot, CalibrationScreen screen, Point park, Point request) {
        robot.mouseMove(park.x, park.y);
        robot.waitForIdle();
        screen.reset();
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

        static Linear fit(int request1, int observed1, int request2, int observed2) {
            if (Math.abs(observed2 - observed1) < 10) {
                throw new JemmyException("mouse pointer did not track the calibration probes (moves to " + request1
                        + " and " + request2 + " were observed at " + observed1 + " and " + observed2 + ")");
            }

            double scale = (double) (observed2 - observed1) / (request2 - request1);
            return new Linear(scale, observed1 - scale * request1);
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

                Point later;
                while ((later = observed.poll()) != null) {
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
