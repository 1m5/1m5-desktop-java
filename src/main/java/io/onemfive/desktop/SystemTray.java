package io.onemfive.desktop;

import io.onemfive.util.Res;
import javafx.application.Platform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * There is no JavaFX support yet, so we need to use AWT.
 */
public class SystemTray {

    private static final Logger LOG = Logger.getLogger(SystemTray.class.getName());

    public static final String INITIALIZING = "Initializing...";
    public static final String STARTING = "Starting...";
    public static final String CONNECTING = "Connecting...";
    public static final String CONNECTED = "Connected";
    public static final String DEGRADED = "Degraded";
    public static final String BLOCKED = "Blocked";
    public static final String RECONNECTING = "Reconnecting...";
    public static final String SHUTTINGDOWN = "Shutting Down...";
    public static final String STOPPED = "Stopped";
    public static final String QUITTING = "Quitting";
    public static final String ERRORED = "Error";

    private static final String SHOW_WINDOW_LABEL = Res.get("systemTray.show");
    private static final String HIDE_WINDOW_LABEL = Res.get("systemTray.hide");

    private String status = INITIALIZING;
    private final Stage stage;
    private final Runnable onExit;
    private final MenuItem toggleShowHideItem = new MenuItem(HIDE_WINDOW_LABEL);
    private TrayIcon trayIcon;

    private MenuItem showItem;
    private MenuItem aboutItem;
    private MenuItem quitItem;

    private Image whiteIcon;
    private Image yellowIcon;
    private Image orangeIcon;
    private Image redIcon;
    private Image blueIcon;
    private Image greenIcon;
    private Image grayIcon;

    public SystemTray(Stage stage, Runnable onExit) {
        this.stage = stage;
        this.onExit = onExit;
    }

    public boolean init() {
        if (!java.awt.SystemTray.isSupported()) {
            LOG.info("System tray is not supported.");
            return false;
        }

        // prevent exiting the app when the last window gets closed
        // For now we allow to close the app by closing the window.
        // Later we only let it close via the system trays exit.
        Platform.setImplicitExit(false);

        showItem = new MenuItem(Res.get("systemTray.show"));
        aboutItem = new MenuItem(Res.get("systemTray.info"));
        quitItem = new MenuItem(Res.get("systemTray.exit"));

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(showItem);
        popupMenu.addSeparator();
        popupMenu.add(aboutItem);
        popupMenu.addSeparator();
        popupMenu.add(toggleShowHideItem);
        popupMenu.addSeparator();
        popupMenu.add(quitItem);
        try {
            trayIcon = new TrayIcon(ImageIO.read(Resources.ICON_WHITE));

            whiteIcon = ImageIO.read(Resources.ICON_WHITE).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            yellowIcon = ImageIO.read(Resources.ICON_YELLOW).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            orangeIcon = ImageIO.read(Resources.ICON_ORANGE).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            redIcon = ImageIO.read(Resources.ICON_RED).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            blueIcon = ImageIO.read(Resources.ICON_BLUE).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            greenIcon = ImageIO.read(Resources.ICON_GREEN).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);
            grayIcon = ImageIO.read(Resources.ICON_GRAY).getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH);

            // On Windows and Linux the icon needs to be resized
            // On macOS we get the correct size from the provided image
//            if (!Utilities.isOSX()) {
//                if (ImageUtil.isRetina()) {
//                    // Using auto sizing provides better results with high resolution
//                    trayIcon.setImageAutoSize(true);
//                } else {
                    // Using scaling provides better results with low resolution
//                    trayIcon = new TrayIcon(whiteIcon.getScaledInstance(trayIcon.getSize().width, -1, Image.SCALE_SMOOTH));
//                }
//            }

            trayIcon.setPopupMenu(popupMenu);
            trayIcon.setToolTip(Res.get("systemTray.tooltip"));

            java.awt.SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException e1) {
            LOG.warning("Icon could not be added to system tray.");
        } catch (IOException e2) {
            LOG.warning(e2.getLocalizedMessage());
        }

        toggleShowHideItem.addActionListener(e -> {
            if (stage.isShowing()) {
                toggleShowHideItem.setLabel(SHOW_WINDOW_LABEL);
                DesktopApp.execute(stage::hide);
            } else {
                toggleShowHideItem.setLabel(HIDE_WINDOW_LABEL);
                DesktopApp.execute(stage::show);
            }
        });

        showItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        DesktopApp.show();
                    }
                });
            }
        });

        // TODO: Have this action navigate to embedded browser and load embedded web site: 1m5://1m5.1m5
//        aboutItem.addActionListener(e -> {
//            try {
//                DesktopApp.execute(() -> navigator);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//        });
        quitItem.addActionListener(e -> DesktopApp.execute(onExit));

        return true;
    }

    public void hideStage() {
        stage.hide();
        toggleShowHideItem.setLabel(SHOW_WINDOW_LABEL);
    }

    public void updateStatus(String status) {
        switch(status) {
            case INITIALIZING: {
                trayIcon.setImage(grayIcon);
                break;
            }
            case STARTING: {
                trayIcon.setImage(yellowIcon);
                break;
            }
            case CONNECTING: {
                showItem.setEnabled(true);
                trayIcon.setImage(orangeIcon);
                break;
            }
            case CONNECTED: {
                trayIcon.setImage(greenIcon);
                break;
            }
            case RECONNECTING: {
                trayIcon.setImage(orangeIcon);
                break;
            }
            case DEGRADED: {
                trayIcon.setImage(yellowIcon);
                break;
            }
            case BLOCKED: {
                trayIcon.setImage(blueIcon);
                break;
            }
            case ERRORED: {
                trayIcon.setImage(redIcon);
                break;
            }
            case SHUTTINGDOWN: {
                trayIcon.setImage(yellowIcon);
                showItem.setEnabled(false);
                break;
            }
            case STOPPED: {
                trayIcon.setImage(grayIcon);
                break;
            }
            case QUITTING: {
                showItem.setEnabled(false);
                quitItem.setEnabled(false);
                trayIcon.setImage(whiteIcon);
                break;
            }
            default: {
                LOG.warning("Status unknown: "+status);
                return;
            }
        }
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
