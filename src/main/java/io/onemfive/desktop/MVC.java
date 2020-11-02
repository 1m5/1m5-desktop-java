package io.onemfive.desktop;

import io.onemfive.data.Envelope;
import io.onemfive.desktop.util.FrameRateTimer;
import io.onemfive.desktop.util.Timer;
import io.onemfive.desktop.views.BaseView;
import io.onemfive.desktop.views.View;
import io.onemfive.network.NetworkService;
import io.onemfive.network.NetworkState;
import io.onemfive.util.DLC;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class MVC {

    private static final Logger LOG = Logger.getLogger(MVC.class.getName());

    private static Class<? extends Timer> timerClass;
    private static Executor executor;

    static {
        executor = Executors.newFixedThreadPool(8);
        timerClass = FrameRateTimer.class;
    }

    public static final Navigation navigation = new Navigation();

    private static final HashMap<String, BaseView> viewCache = new HashMap<>();

    public static Executor getExecutor() {
        return executor;
    }

    public static void setExecutor(Executor executor) {
        MVC.executor = executor;
    }

    public static View loadView(Class<? extends View> viewClass) {
        // Caching on by default
        return loadView(viewClass, true);
    }

    public synchronized static View loadView(Class<? extends View> viewClass, boolean useCache) {
        BaseView view = null;
        if (viewCache.containsKey(viewClass.getName()) && useCache) {
            view = viewCache.get(viewClass.getName());
        } else {
            URL loc = viewClass.getResource(viewClass.getSimpleName()+".fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            try {
                loader.load();
                view = loader.getController();
                if(useCache)
                    viewCache.put(viewClass.getName(), view);
            } catch (IOException e) {
                LOG.warning(e.getLocalizedMessage());
            }
        }
        return view;
    }

    public static void updateNetwork(NetworkState networkState) {
        Envelope e = Envelope.documentFactory();
        DLC.addData(NetworkState.class, networkState, e);
        DLC.addRoute(NetworkService.class, NetworkService.OPERATION_UPDATE_NETWORK_CONFIG, e);
        DesktopService.deliver(e);
    }

    public static void execute(Runnable command) {
        MVC.executor.execute(command);
    }

    // Prefer FxTimer if a delay is needed in a JavaFx class (gui module)
    public static Timer runAfterRandomDelay(Runnable runnable, long minDelayInSec, long maxDelayInSec) {
        return MVC.runAfterRandomDelay(runnable, minDelayInSec, maxDelayInSec, TimeUnit.SECONDS);
    }

    @SuppressWarnings("WeakerAccess")
    public static Timer runAfterRandomDelay(Runnable runnable, long minDelay, long maxDelay, TimeUnit timeUnit) {
        return MVC.runAfter(runnable, new Random().nextInt((int) (maxDelay - minDelay)) + minDelay, timeUnit);
    }

    public static Timer runAfter(Runnable runnable, long delayInSec) {
        return MVC.runAfter(runnable, delayInSec, TimeUnit.SECONDS);
    }

    public static Timer runAfter(Runnable runnable, long delay, TimeUnit timeUnit) {
        return getTimer().runLater(Duration.ofMillis(timeUnit.toMillis(delay)), runnable);
    }

    public static Timer runPeriodically(Runnable runnable, long intervalInSec) {
        return MVC.runPeriodically(runnable, intervalInSec, TimeUnit.SECONDS);
    }

    public static Timer runPeriodically(Runnable runnable, long interval, TimeUnit timeUnit) {
        return getTimer().runPeriodically(Duration.ofMillis(timeUnit.toMillis(interval)), runnable);
    }

    private static Timer getTimer() {
        try {
            return timerClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            String message = "Could not instantiate timer bsTimerClass=" + timerClass;
            LOG.warning(message);
            throw new RuntimeException(message);
        }
    }

}
