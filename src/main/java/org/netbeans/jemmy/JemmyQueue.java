package org.netbeans.jemmy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class exists to allow public access to {@code EventQueue.dispatchEvent()} via a new publc method
 * {@code shortCutEvent()}.
 *
 * @see QueueUtils#processEvent(AWTEvent)
 * @see EventQueue#dispatchEvent(AWTEvent)
 */
public final class JemmyQueue extends EventQueue {
    private static final Logger logger = LoggerFactory.getLogger(JemmyQueue.class);
    private final AtomicBoolean installed = new AtomicBoolean(false);

    private JemmyQueue() {
    }

    public void shortcutEvent(AWTEvent event) {
        super.dispatchEvent(event);
    }

    public void install() {
        if (installed.compareAndSet(false, true)) {
            Runnable runnable = () -> Toolkit.getDefaultToolkit().getSystemEventQueue().push(JemmyQueue.this);
            if (EventQueue.isDispatchThread()) {
                runnable.run();
            } else {
                try {
                    EventQueue.invokeAndWait(runnable);
                } catch (InterruptedException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            logger.info("JemmyQueue installed");
        } else {
            logger.debug("already installed");
        }
    }

    public void uninstall() {
        if (installed.compareAndSet(true, false)) {
            pop();
        } else {
            logger.debug("already uninstalled");
        }
    }

    public static JemmyQueue getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final JemmyQueue instance = new JemmyQueue();
    }
}
