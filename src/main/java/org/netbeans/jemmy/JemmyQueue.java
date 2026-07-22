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
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private JemmyQueue() {}

    public void shortcutEvent(AWTEvent event) {
        super.dispatchEvent(event);
    }

    public void install() {
        // The flag must flip on the EDT, atomically with the push itself. If it flipped on the
        // calling thread instead, an interrupt while waiting for a blocked EDT would roll the
        // flag back with the push runnable still queued; once that stale runnable finally ran,
        // a later install() would push this same queue a second time, and EventQueue.push()
        // then links the queue to itself — an unbreakable cycle that wedges all of AWT.
        Runnable runnable = () -> {
            if (installed.compareAndSet(false, true)) {
                try {
                    Toolkit.getDefaultToolkit().getSystemEventQueue().push(JemmyQueue.this);
                } catch (RuntimeException | Error e) {
                    installed.set(false);
                    throw e;
                }
                logger.info("JemmyQueue installed");
            } else {
                logger.debug("already installed");
            }
        };
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void uninstall() {
        if (installed.compareAndSet(true, false)) {
            try {
                pop();
            } catch (RuntimeException | Error e) {
                // the queue is still pushed; roll the flag back so a later uninstall can retry
                installed.set(true);
                throw e;
            }
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
