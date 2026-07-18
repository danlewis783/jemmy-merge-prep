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
import java.awt.Point;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;

/**
 * Places test windows at a predictable screen location so real robot clicks land inside the test
 * window instead of on desktop icons or other applications. The base location defaults to
 * (300, 300) and can be overridden with the {@code jemmy.testing.window.x} and
 * {@code jemmy.testing.window.y} system properties, which the Gradle test tasks forward from the
 * invoking build (e.g. {@code gradlew userInterfaceTest -Djemmy.testing.window.x=900}).
 */
public final class TestWindows {

    /** Down-right offset between cascaded windows, enough to keep each title bar visible. */
    private static final int CASCADE_STEP = 40;

    private TestWindows() {}

    public static Point baseLocation() {
        return new Point(Integer.getInteger("jemmy.testing.window.x", 300),
                Integer.getInteger("jemmy.testing.window.y", 300));
    }

    /** Places the window at the base location. Call on the event dispatch thread. */
    public static void place(Window window) {
        place(window, 0);
    }

    /**
     * Places the window cascaded down-right from the base location so a series of windows stays
     * near the target area without fully covering each other. Call on the event dispatch thread.
     */
    public static void place(Window window, int cascadeIndex) {
        Point base = baseLocation();
        window.setLocation(base.x + cascadeIndex * CASCADE_STEP, base.y + cascadeIndex * CASCADE_STEP);
    }

    /**
     * Hides and disposes every window the toolkit still knows about. Intended for
     * {@code @AfterEach} so a test class leaves no native resources behind for the next one.
     */
    public static void disposeAll() throws InterruptedException, InvocationTargetException {
        if (EventQueue.isDispatchThread()) {
            disposeAllOnQueue();
        } else {
            EventQueue.invokeAndWait(TestWindows::disposeAllOnQueue);
        }
    }

    private static void disposeAllOnQueue() {
        for (Window window : Window.getWindows()) {
            window.setVisible(false);
            window.dispose();
        }
    }
}
