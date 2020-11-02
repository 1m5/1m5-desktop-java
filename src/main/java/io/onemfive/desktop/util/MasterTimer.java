package io.onemfive.desktop.util;

import io.onemfive.desktop.MVC;

import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;

// Runs all listener objects periodically in a short interval.
public class MasterTimer {

    private final static Logger log = Logger.getLogger(MasterTimer.class.getName());

    private static final java.util.Timer timer = new java.util.Timer();
    // frame rate of 60 fps is about 16 ms but we  don't need such a short interval, 100 ms should be good enough
    public static final long FRAME_INTERVAL_MS = 100;

    static {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MVC.execute(() -> listeners.stream().forEach(Runnable::run));
            }
        }, FRAME_INTERVAL_MS, FRAME_INTERVAL_MS);
    }

    private static final Set<Runnable> listeners = new CopyOnWriteArraySet<>();

    public static void addListener(Runnable runnable) {
        listeners.add(runnable);
    }

    public static void removeListener(Runnable runnable) {
        listeners.remove(runnable);
    }
}
