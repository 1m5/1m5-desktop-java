package io.onemfive.desktop;

import javafx.application.Application;
import javafx.application.Platform;

public class DesktopAppMain {

    private static DesktopApp app;

    public static void main(String[] args) {
        // For some reason the JavaFX launch process results in us losing the thread
        // context class loader: reset it. In order to work around a bug in JavaFX 8u25
        // and below, you must include the following code as the first line of your
        // realMain method:
        Thread.currentThread().setContextClassLoader(DesktopAppMain.class.getClassLoader());

        MVC.setExecutor(Platform::runLater);

        DesktopApp.appLaunchedHandler = application -> {
            app = (DesktopApp)application;
            // Map to user thread!
            MVC.execute(app::init);
        };

        Application.launch(DesktopApp.class);
    }
}
