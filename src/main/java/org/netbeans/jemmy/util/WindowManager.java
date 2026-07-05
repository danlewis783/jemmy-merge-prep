package org.netbeans.jemmy.util;

import org.netbeans.jemmy.TimeoutKey;
import org.netbeans.jemmy.Timeouts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WindowManager {
    private static final Logger logger = LoggerFactory.getLogger(WindowManager.class);
    private final List<WindowJobPerformer> jobs;

    private WindowManager() {
        jobs = new ArrayList<>();
    }

    public void add(WindowFunction job) {
        synchronized (jobs) {
            WindowJobPerformer performer = new WindowJobPerformer(job);
            jobs.add(performer);
            Executors.newSingleThreadExecutor().submit(performer);
        }
    }

    public void remove(WindowFunction job) {
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

    private boolean performJobOnce(WindowFunction job) {
        Window win = org.netbeans.jemmy.functions.WindowFunction.getWindow(null, job.getPredicate(), 0);
        if (win != null) {
            job.apply(win);

            return true;
        } else {
            return false;
        }
    }

    private static WindowManager getInstance() {
        return Holder.instance;
    }

    public static void addJob(WindowFunction job) {
        getInstance().add(job);
    }

    private static class Holder {
        private static final WindowManager instance = new WindowManager();
    }


    private static class WindowJobPerformer implements Callable<Void> {
        private final AtomicBoolean needStop = new AtomicBoolean(false);
        private final WindowFunction job;

        private WindowJobPerformer(WindowFunction job) {
            this.job = job;
        }

        private void stop() {
            if (!needStop.compareAndSet(false, true)) {
                logger.warn("already marked as needing stop");
            }
        }

        @Override
        public Void call() throws Exception {
            if (needStop.get()) {
                throw new IllegalStateException("attempt to run but already marked stop");
            }

            while (!needStop.get()) {
                WindowManager.getInstance().performJobOnce(job);
                Timeouts.sleep(TimeoutKey.WindowManager_TimeDelta);
            }

            return null;
        }
    }
}
