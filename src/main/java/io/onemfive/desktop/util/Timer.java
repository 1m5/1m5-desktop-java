package io.onemfive.desktop.util;

import java.time.Duration;

public interface Timer {

    Timer runLater(Duration delay, Runnable action);

    Timer runPeriodically(Duration interval, Runnable runnable);

    void stop();
}
