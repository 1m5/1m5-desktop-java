package io.onemfive.desktop;

import io.onemfive.desktop.user.Preferences;
import io.onemfive.desktop.util.ImageUtil;
import io.onemfive.desktop.views.home.HomeView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ra.common.DLC;
import ra.common.Envelope;
import ra.common.service.ServiceNotAccessibleException;
import ra.common.service.ServiceNotSupportedException;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.util.AppThread;
import ra.util.Config;
import ra.util.LocaleUtil;
import ra.util.Wait;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static io.onemfive.desktop.CssTheme.CSS_THEME_DARK;
import static io.onemfive.desktop.CssTheme.CSS_THEME_LIGHT;
import static io.onemfive.desktop.util.Layout.*;

public class DesktopApp extends Application implements Thread.UncaughtExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DesktopApp.class.getName());

    private static SystemTray systemTray;
    private static boolean systemTrayInitialized = false;
    static Consumer<Application> appLaunchedHandler;

    public static Runnable shutDownHandler;

    public static double WIDTH;
    public static double HEIGHT;

    private static Stage stage;
    private boolean popupOpened;
    public static Scene scene;
    private boolean shutDownRequested;
    private boolean shutdownOnException = true;
    private ServiceStatus uiServiceStatus = ServiceStatus.NOT_INITIALIZED;

    private Properties properties;
    private AppThread busThread;

    public DesktopApp() {
        shutDownHandler = this::stop;
    }

    @Override
    public void init() {
        LOG.info("DesktopApp initializing...\n\tThread name: " + Thread.currentThread().getName());
        LocaleUtil.currentLocale = Locale.US; // Default - TODO: load locale from preferences
        // Launch Service Bus
        // TODO: Check to see if Service Bus already running. If so, use it.
        try {
            properties = Config.loadFromClasspath("onemfive-desktop.config");
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
        }

        busThread = new AppThread(() -> BusClient.start(properties));
        busThread.setDaemon(true);
        busThread.start();

        Wait.aSec(500); // Give some room for the bus to startup

        // Register DesktopService
        try {
            Map<String, List<ServiceStatusObserver>> observers = new HashMap<>();
            observers.put(DesktopService.class.getName(), Arrays.asList(serviceStatus -> uiServiceStatus = serviceStatus));
            BusClient.registerService(DesktopService.class, properties, observers.get(DesktopService.class.getName()));
        } catch (ServiceNotAccessibleException e) {
            LOG.severe(e.getLocalizedMessage());
        } catch (ServiceNotSupportedException e) {
            LOG.severe(e.getLocalizedMessage());
        }

        shutDownHandler = new Runnable() {
            @Override
            public void run() {
                stop();
            }
        };

        // Setup Preferences
        // TODO: Load from persistence
        Preferences.useAnimations = true;
        Preferences.cssTheme = CSS_THEME_LIGHT;
        Preferences.locale = Locale.US;
    }

    @Override
    public void start(Stage s) {
        LOG.info("DesktopApp starting...\n\tThread name: " + Thread.currentThread().getName());
        stage = s;

        HomeView homeView = (HomeView) MVC.loadView(HomeView.class, true);
        Rectangle maxWindowBounds = new Rectangle();
        try {
            maxWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        } catch (IllegalArgumentException e) {
            // Multi-screen environments may encounter IllegalArgumentException (Window must not be zero)
            // Just ignore the exception and continue, which means the window will use the minimum window size below
            // since we are unable to determine if we can use a larger size
        }
        WIDTH = maxWindowBounds.width < INITIAL_WINDOW_WIDTH ?
                Math.max(maxWindowBounds.width, MIN_WINDOW_WIDTH) :
                INITIAL_WINDOW_WIDTH;
        HEIGHT = maxWindowBounds.height < INITIAL_WINDOW_HEIGHT ?
                Math.max(maxWindowBounds.height, MIN_WINDOW_HEIGHT) :
                INITIAL_WINDOW_HEIGHT;
        scene = new Scene((StackPane)homeView.getRoot(), WIDTH, HEIGHT);

        CssTheme.loadSceneStyles(scene, Preferences.cssTheme);

        // Launch Tray
//        systemTray = new SystemTray(stage, this::stop);
//        systemTrayInitialized = systemTray.init();

        // configure the primary stage
        stage.setOnCloseRequest(event -> {
            event.consume();
            stop();
        });
        stage.setTitle("1M5");
        stage.setScene(scene);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.getIcons().add(ImageUtil.getApplicationIconImage());

        // make the UI visible
        if(!systemTrayInitialized)
            show();
    }

    @Override
    public void stop() {
        if (!shutDownRequested) {
//            new Popup().headLine(Res.get("popup.shutDownInProgress.headline"))
//                    .backgroundInfo(Res.get("popup.shutDownInProgress.msg"))
//                    .hideCloseButton()
//                    .useAnimation(false)
//                    .show();
//            UserThread.runAfter(() -> {
//                gracefulShutDownHandler.gracefulShutDown(() -> {
//                    log.debug("App shutdown complete");
//                });
//            }, 200, TimeUnit.MILLISECONDS);
            shutDownRequested = true;
            BusClient.shutdown(false);
            Platform.exit();
        }
    }

    public static void execute(Runnable runnable) {
        Platform.runLater(runnable);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!shutDownRequested) {
            if (scene == null) {
                LOG.warning("Scene not available yet, we create a new scene. The bug might be caused by an exception in a constructor or by a circular dependency in Guice. throwable=" + throwable.toString());
                scene = new Scene(new StackPane(), 1000, 650);
                CssTheme.loadSceneStyles(scene, CssTheme.CSS_THEME_LIGHT);
                stage.setScene(scene);
                stage.show();
            }
            try {
                if (shutdownOnException)
                    stop();
            } catch (Throwable throwable2) {
                // If printStackTrace cause a further exception we don't pass the throwable to the Popup.
                LOG.severe(throwable2.getLocalizedMessage());
                stop();
            }
        }
    }

    public static void show() {
        stage.show();
    }

}
