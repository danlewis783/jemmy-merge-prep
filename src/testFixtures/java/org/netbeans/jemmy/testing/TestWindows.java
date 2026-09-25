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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.UIManager;
import org.jetbrains.annotations.Nullable;

/**
 * Places test windows at a predictable screen location so real robot clicks land inside the test
 * window instead of on desktop icons or other applications. The base location defaults to
 * (300, 300) and can be overridden with the {@code jemmy.testing.window.x} and
 * {@code jemmy.testing.window.y} system properties, which the Gradle test tasks set from the Gradle
 * properties of the same name (e.g. {@code gradlew userInterfaceTest -Pjemmy.testing.window.x=900}).
 * Placed windows are also titled with the running test's name, which
 * {@link JemmyStateResetExtension} records.
 */
public final class TestWindows {

    /** Down-right offset between cascaded windows, enough to keep each title bar visible. */
    private static final int CASCADE_STEP = 40;

    /**
     * Title-bar width besides the title text (icon, caption buttons, padding), in multiples of the
     * caption font size. Windows 11 at 100% scaling (12-pixel caption font, three 46-pixel caption
     * buttons) truncates titles once less than about 200 pixels remain; 20 ems leaves a margin.
     */
    private static final int TITLE_BAR_CHROME_EMS = 20;

    private static volatile @Nullable String currentTestName;

    private TestWindows() {}

    public static Point baseLocation() {
        return new Point(Integer.getInteger("jemmy.testing.window.x", 300),
                Integer.getInteger("jemmy.testing.window.y", 300));
    }

    /**
     * The running test as {@code TestClass.testMethod}, or just {@code TestClass} outside a test
     * method (e.g. in {@code @BeforeAll}); null when no test class registered
     * {@link JemmyStateResetExtension}.
     */
    public static @Nullable String currentTestName() {
        return currentTestName;
    }

    static void setCurrentTestName(@Nullable String testName) {
        currentTestName = testName;
    }

    /**
     * Places the window at the base location and labels it; see {@link #place(Window, int)}.
     * Call on the event dispatch thread.
     */
    public static void place(Window window) {
        place(window, 0);
    }

    /**
     * Places the window cascaded down-right from the base location so a series of windows stays
     * near the target area without fully covering each other. Call on the event dispatch thread.
     *
     * <p>So someone watching the suite can tell which test is running, an untitled frame or dialog
     * is titled with {@link #currentTestName()}, and a decorated window is made wide enough for its
     * whole title to show. The width is enforced as a minimum size until the window first opens,
     * so {@code setSize} and {@code pack} calls made after this one are widened too, while
     * resizing the open window (which some tests do on purpose) is unrestricted.
     */
    public static void place(Window window, int cascadeIndex) {
        Point base = baseLocation();
        window.setLocation(base.x + cascadeIndex * CASCADE_STEP, base.y + cascadeIndex * CASCADE_STEP);
        String title = titleWithTestName(window);
        if ((title != null) && !title.isEmpty()) {
            widenToFitTitle(window, title);
        }
    }

    /** Titles an untitled frame or dialog with the test name; returns the resulting title. */
    private static @Nullable String titleWithTestName(Window window) {
        String testName = currentTestName;
        if (window instanceof Frame) {
            Frame frame = (Frame) window;
            if (frame.isUndecorated()) {
                return null;
            }
            if (isBlank(frame.getTitle()) && (testName != null)) {
                frame.setTitle(testName);
            }
            return frame.getTitle();
        }
        if (window instanceof Dialog) {
            Dialog dialog = (Dialog) window;
            if (dialog.isUndecorated()) {
                return null;
            }
            if (isBlank(dialog.getTitle()) && (testName != null)) {
                dialog.setTitle(testName);
            }
            return dialog.getTitle();
        }

        return null;
    }

    private static boolean isBlank(@Nullable String title) {
        return (title == null) || title.trim().isEmpty();
    }

    private static void widenToFitTitle(Window window, String title) {
        Font font = captionFont();
        int width = window.getFontMetrics(font).stringWidth(title) + TITLE_BAR_CHROME_EMS * font.getSize();
        if (window.isShowing()) {
            if (window.getWidth() < width) {
                window.setSize(width, window.getHeight());
            }
            return;
        }

        Dimension previousMinimum = window.isMinimumSizeSet() ? window.getMinimumSize() : null;
        int minimumHeight = (previousMinimum == null) ? 0 : previousMinimum.height;
        if ((previousMinimum != null) && (previousMinimum.width >= width)) {
            return;
        }

        // Window.setSize, setBounds and pack all enlarge to the minimum size
        window.setMinimumSize(new Dimension(width, minimumHeight));
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                window.removeWindowListener(this);
                window.setMinimumSize(previousMinimum);
            }
        });
    }

    private static Font captionFont() {
        Object windowsCaptionFont = Toolkit.getDefaultToolkit().getDesktopProperty("win.frame.captionFont");
        if (windowsCaptionFont instanceof Font) {
            return (Font) windowsCaptionFont;
        }

        Font labelFont = UIManager.getFont("Label.font");
        return (labelFont != null) ? labelFont : new Font(Font.DIALOG, Font.PLAIN, 12);
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
