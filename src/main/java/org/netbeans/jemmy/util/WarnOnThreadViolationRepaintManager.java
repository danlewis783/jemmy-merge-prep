package org.netbeans.jemmy.util;

import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WarnOnThreadViolationRepaintManager extends CheckThreadViolationRepaintManager {
    private static final Logger logger = LoggerFactory.getLogger(WarnOnThreadViolationRepaintManager.class);
    private static boolean enableInstallOptimization = false;

    public WarnOnThreadViolationRepaintManager() {}

    public WarnOnThreadViolationRepaintManager(boolean completeCheck) {
        super(completeCheck);
    }

    @Override
    void violationFound(JComponent c, StackTraceElement[] stackTraceElements) {
        logger.warn("EDT violation detected\n" + asString(stackTraceElements));
    }

    private String asString(StackTraceElement[] stackTraceElements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append("\tat ");
            sb.append(stackTraceElement.toString());
            sb.append('\n');
        }

        return sb.toString();
    }

    public static void setEnableInstallOptimization(boolean val) {
        WarnOnThreadViolationRepaintManager.enableInstallOptimization = val;
    }

    public static WarnOnThreadViolationRepaintManager install() {
        if (enableInstallOptimization) {
            Object m = currentManager(null);
            if (m instanceof WarnOnThreadViolationRepaintManager) {
                return (WarnOnThreadViolationRepaintManager) m;
            }
        }

        return installNew();
    }

    private static WarnOnThreadViolationRepaintManager installNew() {
        WarnOnThreadViolationRepaintManager m = new WarnOnThreadViolationRepaintManager();
        setCurrentManager(m);

        return m;
    }
}
