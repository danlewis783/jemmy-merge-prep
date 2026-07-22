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
package org.netbeans.jemmy;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.EnumSet;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class QueueUtils {
    private static final Logger logger = LoggerFactory.getLogger(QueueUtils.class);

    private static volatile boolean isInitialized = false;
    private static volatile boolean isShortcutMode = false;
    private static volatile @Nullable JemmyQueue jemmyQueue;

    private static synchronized void initialize() {
        // double check the guard before initializing
        if (isInitialized) {
            return;
        }
        JemmyContext jemmyContext = JemmyContext.getInstance();
        EnumSet<DispatchingModel> dispatchingModel = jemmyContext.getDispatchingModel();
        boolean isShortcutMode = dispatchingModel.contains(DispatchingModel.Shortcut);
        if (isShortcutMode) {
            JemmyQueue jemmyQueue = JemmyQueue.getInstance();
            jemmyQueue.install();
            QueueUtils.jemmyQueue = jemmyQueue;
        }
        QueueUtils.isShortcutMode = isShortcutMode;
        QueueUtils.isInitialized = true;
        logger.info("QueueUtils initialized");
    }

    public static void processEvent(AWTEvent event) {
        if (!isInitialized) {
            initialize();
        }

        if (isShortcutMode && EventQueue.isDispatchThread()) {
            Objects.requireNonNull(jemmyQueue, "jemmyQueue not initialized").shortcutEvent(event);
        } else {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
        }
    }

    /**
     * Uninstalls the {@link JemmyQueue} if one was installed and forgets the initialization, so the
     * next {@link #processEvent(AWTEvent)} re-reads the dispatching model from
     * {@link JemmyContext} and installs (or not) accordingly. {@link JemmyContext} calls this on
     * every model switch, keeping the queue installation consistent with the active model; it is
     * also the reset for the queue installation status on its own.
     */
    public static synchronized void reset() {
        JemmyQueue queue = jemmyQueue;
        if (queue != null) {
            // uninstall before forgetting: if it fails, every field is untouched and the old
            // state stays fully consistent
            queue.uninstall();
        }
        jemmyQueue = null;
        isShortcutMode = false;
        isInitialized = false;
    }
}
