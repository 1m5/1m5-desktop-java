package io.onemfive.desktop;

import io.onemfive.desktop.user.Preferences;
import io.onemfive.desktop.util.ImageUtil;
import io.onemfive.desktop.views.home.HomeView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import ra.common.service.ServiceStatus;
import ra.common.Config;
import ra.common.LocaleUtil;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static io.onemfive.desktop.ClientType.MOBILE;
import static io.onemfive.desktop.ClientType.TAB;
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
    private DesktopClient desktopClient;

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

        // Initialize Desktop Bus Client
        desktopClient = DesktopClient.getInstance(properties);
        desktopClient.start();

        // Load maven model
        MavenXpp3Reader reader = new MavenXpp3Reader();
        try {
            Model model = reader.read(new FileReader("pom.xml"));
//            System.out.println(model.getId());
//            System.out.println(model.getGroupId());
//            System.out.println(model.getArtifactId());
            LOG.info("1M5 version: "+model.getVersion());
            DesktopClient.setGlobal("1m5.version", model.getVersion());
        } catch (Exception e) {
            DesktopClient.setGlobal("1m5.version", "?");
        }
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
        if(DesktopClient.getClientType()==MOBILE || DesktopClient.getClientType()==TAB) {
            WIDTH = maxWindowBounds.width;
            HEIGHT = maxWindowBounds.height;
        } else {
            WIDTH = maxWindowBounds.width < DESKTOP_INITIAL_WINDOW_WIDTH ?
                    Math.max(maxWindowBounds.width, DESKTOP_MIN_WINDOW_WIDTH) :
                    DESKTOP_INITIAL_WINDOW_WIDTH;
            HEIGHT = maxWindowBounds.height < DESKTOP_INITIAL_WINDOW_HEIGHT ?
                    Math.max(maxWindowBounds.height, DESKTOP_MIN_WINDOW_HEIGHT) :
                    DESKTOP_INITIAL_WINDOW_HEIGHT;
        }

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
        stage.setMinWidth(DESKTOP_MIN_WINDOW_WIDTH);
        stage.setMinHeight(DESKTOP_MIN_WINDOW_HEIGHT);
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
