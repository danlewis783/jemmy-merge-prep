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

import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.lang.ref.WeakReference;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import org.jspecify.annotations.Nullable;

/**
 * Detects Swing components touched off the event dispatch thread, in the style of Alexander
 * Potochkin's thread-violation checker. Only {@code repaint} is treated as a thread-safe entry
 * point; {@code revalidate}/{@code invalidate} reached off the dispatch thread are reported.
 *
 * <p>{@link #violationFound} runs on the violating thread, not the test thread: a violation
 * raised from an application worker thread only fails a test if that thread's exceptions are
 * observed. With {@code completeCheck} false, violations on components that are not yet showing
 * are ignored.
 */
public abstract class CheckThreadViolationRepaintManager extends RepaintManager {
    private static @Nullable RepaintManager replacedManager;

    private final boolean completeCheck;
    private volatile @Nullable WeakReference<JComponent> lastComponent;

    protected CheckThreadViolationRepaintManager() {
        this(true);
    }

    protected CheckThreadViolationRepaintManager(boolean completeCheck) {
        this.completeCheck = completeCheck;
    }

    final boolean isCompleteCheck() {
        return completeCheck;
    }

    /**
     * Makes an instance of {@code type} the current repaint manager and remembers the manager it
     * replaced. When {@code reuseCurrent} is true and the current manager is already a
     * {@code type} doing the complete check, it is returned unchanged.
     */
    static synchronized <T extends CheckThreadViolationRepaintManager> T install(
            Class<T> type, boolean reuseCurrent, Supplier<T> factory) {
        RepaintManager current = currentManager(null);
        if (reuseCurrent && type.isInstance(current) && type.cast(current).isCompleteCheck()) {
            return type.cast(current);
        }

        if (!(current instanceof CheckThreadViolationRepaintManager)) {
            replacedManager = current;
        }

        T manager = factory.get();
        setCurrentManager(manager);
        return manager;
    }

    /**
     * Restores the repaint manager that was current before the first {@code install()}, or a
     * default {@link RepaintManager} if none was recorded. No-op unless a checker is installed.
     */
    public static synchronized void uninstall() {
        if (currentManager(null) instanceof CheckThreadViolationRepaintManager) {
            RepaintManager restored = (replacedManager != null) ? replacedManager : new RepaintManager();
            replacedManager = null;
            setCurrentManager(restored);
        }
    }

    @Override
    public synchronized void addInvalidComponent(JComponent component) {
        checkThreadViolations(component);
        super.addInvalidComponent(component);
    }

    @Override
    public void addDirtyRegion(JComponent component, int x, int y, int w, int h) {
        checkThreadViolations(component);
        super.addDirtyRegion(component, x, y, w, h);
    }

    private void checkThreadViolations(JComponent c) {
        if (!isEventDispatchThread() && (completeCheck || c.isShowing())) {
            boolean imageUpdate = false;
            boolean repaint = false;
            boolean fromSwing = false;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (repaint && isInvalidDuringRepaint(st.getClassName())) {
                    fromSwing = true;
                }

                if (repaint && "imageUpdate".equals(st.getMethodName())) {
                    imageUpdate = true;
                }

                if ("repaint".equals(st.getMethodName())) {
                    repaint = true;
                    fromSwing = false;
                }
            }

            if (imageUpdate) {
                return;
            }

            // repaint() is thread-safe unless Swing itself repainted during another operation
            if (repaint && !fromSwing) {
                return;
            }

            WeakReference<JComponent> last = lastComponent;
            if ((last != null) && (c == last.get())) {
                return;
            }

            lastComponent = new WeakReference<>(c);
            violationFound(c, stackTrace);
        }
    }

    private boolean isInvalidDuringRepaint(String className) {
        if (className.startsWith("javax.swing.SwingWorker")) {
            return false;
        }

        return className.startsWith("javax.swing.");
    }

    protected abstract void violationFound(JComponent c, StackTraceElement[] stackTrace);
}
