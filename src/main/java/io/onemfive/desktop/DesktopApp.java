package io.onemfive.desktop;

import onemfive.Platform;
import io.onemfive.core.ServiceStatus;
import io.onemfive.core.ServiceStatusObserver;
import io.onemfive.core.admin.AdminService;
import io.onemfive.data.Envelope;
import io.onemfive.desktop.user.Preferences;
import io.onemfive.desktop.util.ImageUtil;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.util.AppThread;
import io.onemfive.util.DLC;
import io.onemfive.util.LocaleUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    private static Platform platform;

    public DesktopApp() {
        shutDownHandler = this::stop;
    }

    @Override
    public void init() {
        LOG.info("DesktopApp initializing...\n\tThread name: " + Thread.currentThread().getName());
        LocaleUtil.currentLocale = Locale.US; // Default - TODO: load locale from preferences
        // Launch Router
        String[] args = {};
        platform = new Platform();
        AppThread routerThread = new AppThread(() -> platform.start(args));
        routerThread.setDaemon(true);
        routerThread.start();

        // Register UIService
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, List<ServiceStatusObserver>> observers = new HashMap<>();
                observers.put(DesktopService.class.getName(), Arrays.asList(new ServiceStatusObserver() {
                    @Override
                    public void statusUpdated(ServiceStatus serviceStatus) {
                        uiServiceStatus = serviceStatus;
                    }
                }));
                Envelope e = Envelope.documentFactory();
                DLC.addData(ServiceStatusObserver.class, observers, e);
                DLC.addEntity(Arrays.asList(DesktopService.class),e);
                DLC.addRoute(AdminService.class, AdminService.OPERATION_REGISTER_SERVICES, e);
                Platform.sendRequest(e);
            }
        }).start();

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
            Platform.stop();
            javafx.application.Platform.exit();
        }
    }

    public static void execute(Runnable runnable) {
        javafx.application.Platform.runLater(runnable);
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
