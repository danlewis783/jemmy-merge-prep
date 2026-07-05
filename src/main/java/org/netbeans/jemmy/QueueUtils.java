package org.netbeans.jemmy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.EnumSet;

public final class QueueUtils {
    private static final Logger logger = LoggerFactory.getLogger(QueueUtils.class);

    private static volatile boolean isInitialized = false;
    private static volatile boolean isShortcutMode = false;
    private static volatile JemmyQueue jemmyQueue;

    private static synchronized void initialize() {
        // double check the guard before initializing
        if (isInitialized) {
            return;
        }
        JemmyProperties jemmyProperties = JemmyProperties.getInstance();
        EnumSet<DispatchingModel> dispatchingModel = jemmyProperties.getDispatchingModel();
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
        if (! isInitialized) {
            initialize();
        }

        if (isShortcutMode && EventQueue.isDispatchThread()) {
            jemmyQueue.shortcutEvent(event);
        } else {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
        }
    }
}
