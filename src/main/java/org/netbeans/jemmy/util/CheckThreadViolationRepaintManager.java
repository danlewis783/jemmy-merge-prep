package org.netbeans.jemmy.util;

import static javax.swing.SwingUtilities.isEventDispatchThread;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import javax.swing.*;
import org.jspecify.annotations.Nullable;

abstract class CheckThreadViolationRepaintManager extends RepaintManager {
    private final boolean completeCheck;
    private @Nullable WeakReference<JComponent> lastComponent;

    CheckThreadViolationRepaintManager() {
        this(true);
    }

    CheckThreadViolationRepaintManager(boolean completeCheck) {
        this.completeCheck = completeCheck;
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
            boolean isSafe = false;
            boolean fromSwing = false;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement st : stackTrace) {
                if (isSafe && isInvalidDuringRepaint(st.getClassName())) {
                    fromSwing = true;
                }

                if (isSafe && "imageUpdate".equals(st.getMethodName())) {
                    imageUpdate = true;
                }

                if (isSafe(st.getMethodName())) {
                    isSafe = true;
                    fromSwing = false;
                }
            }

            if (imageUpdate) {
                return;
            }

            if (isSafe && !fromSwing) {
                return;
            }

            if ((lastComponent != null) && (c == lastComponent.get())) {
                return;
            }

            lastComponent = new WeakReference<>(c);
            violationFound(c, stackTrace);
        }
    }

    private boolean isSafe(String methodName) {
        return Arrays.asList("repaint", "revalidate", "invalidate").contains(methodName);
    }

    private boolean isInvalidDuringRepaint(String className) {
        if (className.startsWith("javax.swing.SwingWorker")) {
            return false;
        }

        return className.startsWith("javax.swing.");
    }

    abstract void violationFound(JComponent c, StackTraceElement[] stackTrace);
}
