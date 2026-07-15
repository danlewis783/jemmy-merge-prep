package org.netbeans.jemmy.util;

import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailOnThreadViolationRepaintManager extends CheckThreadViolationRepaintManager {
    private static final Logger logger = LoggerFactory.getLogger(FailOnThreadViolationRepaintManager.class);
    private static boolean enableInstallOptimization = true;

    public FailOnThreadViolationRepaintManager() {}

    public FailOnThreadViolationRepaintManager(boolean completeCheck) {
        super(completeCheck);
    }

    @Override
    void violationFound(JComponent c, StackTraceElement[] stackTraceElements) {
        EdtViolationException e = new EdtViolationException("EDT violation detected");
        if (stackTraceElements != null) {
            e.setStackTrace(stackTraceElements);
        }

        throw e;
    }

    public static void setEnableInstallOptimization(boolean val) {
        FailOnThreadViolationRepaintManager.enableInstallOptimization = val;
    }

    public static FailOnThreadViolationRepaintManager install() {
        if (enableInstallOptimization) {
            Object m = currentManager(null);
            if (m instanceof FailOnThreadViolationRepaintManager) {
                return (FailOnThreadViolationRepaintManager) m;
            }
        }

        return installNew();
    }

    private static FailOnThreadViolationRepaintManager installNew() {
        FailOnThreadViolationRepaintManager m = new FailOnThreadViolationRepaintManager();
        setCurrentManager(m);

        return m;
    }
}
