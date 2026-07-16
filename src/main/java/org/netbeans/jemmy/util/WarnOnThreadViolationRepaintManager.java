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

import javax.swing.JComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repaint manager that logs a warning, with the violating stack trace, when a component is
 * touched off the event dispatch thread.
 */
public class WarnOnThreadViolationRepaintManager extends CheckThreadViolationRepaintManager {
    private static final Logger logger = LoggerFactory.getLogger(WarnOnThreadViolationRepaintManager.class);
    private static volatile boolean enableInstallOptimization = true;

    public WarnOnThreadViolationRepaintManager() {}

    public WarnOnThreadViolationRepaintManager(boolean completeCheck) {
        super(completeCheck);
    }

    @Override
    protected void violationFound(JComponent c, StackTraceElement[] stackTraceElements) {
        if (logger.isWarnEnabled()) {
            logger.warn("EDT violation detected\n{}", asString(stackTraceElements));
        }
    }

    private static String asString(StackTraceElement[] stackTraceElements) {
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
        return install(
                WarnOnThreadViolationRepaintManager.class,
                enableInstallOptimization,
                WarnOnThreadViolationRepaintManager::new);
    }
}
