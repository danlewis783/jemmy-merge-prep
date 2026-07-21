/*
 * Copyright (c) 1997, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.netbeans.jemmy.util;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WindowManager {
    private static final Logger logger = LoggerFactory.getLogger(WindowManager.class);
    private final List<WindowJobPerformer> jobs;

    private WindowManager() {
        jobs = new ArrayList<>();
    }

    public void add(WindowFunction<?> job) {
        synchronized (jobs) {
            WindowJobPerformer performer = new WindowJobPerformer(job);
            jobs.add(performer);
            Executors.newSingleThreadExecutor().submit(performer);
        }
    }

    public void remove(WindowFunction<?> job) {
        List<WindowJobPerformer> stopList = new ArrayList<>();
        synchronized (jobs) {
            for (WindowJobPerformer performer : jobs) {
                if (performer.job == job) {
                    performer.stop();
                    stopList.add(performer);
                }
            }

            jobs.removeAll(stopList);
        }
    }

    private boolean performJobOnce(WindowFunction<?> job) {
        Window win = org.netbeans.jemmy.functions.WindowFunction.getWindow(null, job.getPredicate(), 0);
        if (win != null) {
            apply(job, win);

            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked") // the job's predicate only matches windows of its own F type
    private static <F> void apply(WindowFunction<F> job, Window win) {
        job.apply((F) win);
    }

    private static WindowManager getInstance() {
        return Holder.instance;
    }

    public static void addJob(WindowFunction<?> job) {
        getInstance().add(job);
    }

    private static class Holder {
        private static final WindowManager instance = new WindowManager();
    }

    private static class WindowJobPerformer implements Callable<Void> {
        private final AtomicBoolean needStop = new AtomicBoolean(false);
        private final WindowFunction<?> job;

        private WindowJobPerformer(WindowFunction<?> job) {
            this.job = job;
        }

        private void stop() {
            if (!needStop.compareAndSet(false, true)) {
                logger.warn("already marked as needing stop");
            }
        }

        @Override
        public Void call() {
            if (needStop.get()) {
                throw new IllegalStateException("attempt to run but already marked stop");
            }

            while (!needStop.get()) {
                try {
                    WindowManager.getInstance().performJobOnce(job);
                } catch (RuntimeException e) {
                    // a transient failure must not kill the job: nothing ever queries the
                    // executor's future, so a propagated exception would vanish silently and
                    // the job would simply stop performing
                    logger.warn("window job iteration failed; retrying", e);
                }

                Timeouts.sleep(TimeoutKey.WindowManager_TimeDelta);
            }

            return null;
        }
    }
}
