package io.onemfive.desktop.views.commons.browser;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.Navigation;
import io.onemfive.desktop.OneMFiveResources;
import io.onemfive.desktop.util.KeystrokeUtil;
import io.onemfive.desktop.views.ActivatableView;
import io.onemfive.desktop.views.ViewPath;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import onemfive.ManCon;
import onemfive.ManConStatus;

import java.net.URL;

public class BrowserView extends ActivatableView {

    private Scene scene;

    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final WebHistory history = engine.getHistory();

    private EventHandler<KeyEvent> keyEventEventHandler;

    private JFXTextField urlTextField;

    private Navigation.Listener navigationListener;

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        super.initialize();

        navigationListener = new Navigation.Listener() {
            @Override
            public void onNavigationRequested(ViewPath path) {
                // ignore
            }

            @Override
            public void onNavigationRequested(ViewPath path, Object data) {
                LOG.info("Path: "+data);
                if(data!=null)
                    handlePath((String)data);
            }
        };
        MVC.navigation.addListener(navigationListener);

        BorderPane rootContainer = (BorderPane) root;
        engine.setJavaScriptEnabled(true);

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(5);
        rootContainer.setCenter(vBox);

        HBox nav = new HBox();
        nav.setPadding(new Insets(5));
        nav.setSpacing(5);
        vBox.getChildren().add(nav);

        urlTextField = new JFXTextField();
        urlTextField.setText("1m5://1m5.1m5");

        HBox.setHgrow(urlTextField, Priority.ALWAYS);
        nav.getChildren().add(urlTextField);

        JFXButton go = new JFXButton("Go");
        go.getStyleClass().add("button-raised");
        go.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String path = urlTextField.getText();
                handlePath(path);
            }
        });
        nav.getChildren().add(go);

        JFXButton refresh = new JFXButton("Refresh");
        refresh.getStyleClass().add("button-raised");
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("Refresh: "+ urlTextField.getText());
                engine.reload();
                LOG.info(urlTextField.getText()+" refreshed");
            }
        });
        nav.getChildren().add(refresh);

        JFXButton back = new JFXButton("Back");
        back.getStyleClass().add("button-raised");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("Back...");
                history.go(-1);
                LOG.info("Backed");
            }
        });
        nav.getChildren().add(back);

        JFXButton stop = new JFXButton("Stop");
        stop.getStyleClass().add("button-raised");
        stop.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("Cancel...");
                engine.getLoadWorker().cancel();
                LOG.info("Canceled");
            }
        });
        nav.getChildren().add(stop);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(webView);
        // TODO: Change this to auto-fill space
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        webView.setPrefWidth(1200);

        vBox.getChildren().add(scrollPane);

        engine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            String loc = engine.getLocation();
                            if(!loc.startsWith("file:")) {
                                urlTextField.setText(loc);
                            }
                        }
                    }
                });

        engine.load(OneMFiveResources.IMS_WEB_INDEX.toExternalForm());

        history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>() {
                 @Override
                 public void onChanged(Change<? extends WebHistory.Entry> c) {
                     c.next();
                     for (WebHistory.Entry e : c.getRemoved()) {
                         LOG.info(e.getUrl());
                     }
                     for (WebHistory.Entry e : c.getAddedSubList()) {
                         LOG.info(e.getUrl());
                     }
                 }
             }
        );
        history.go(0);

        keyEventEventHandler = keyEvent -> {
            if(KeystrokeUtil.isAltOrCtrlPressed(KeyCode.ENTER, keyEvent)) {
                refresh.fire();
            } else if(keyEvent.getCode()==KeyCode.ENTER) {
                go.fire();
            }
        };

        LOG.info("Initialized.");
    }

    @Override
    protected void activate() {
        if (root.getScene() != null) {
            scene = root.getScene();
            scene.addEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
        }
        LOG.info("Activated");
    }

    @Override
    protected void deactivate() {
        if (scene != null)
            scene.removeEventHandler(KeyEvent.KEY_RELEASED, keyEventEventHandler);
        LOG.info("Deactivated");
    }

    private void handlePath(String url) {
        LOG.info("Go: "+url);
        URL newURL;
        String path;
        if(url.toLowerCase().startsWith("1m5:")) {
            LOG.info("No proxy used - P2P web.");
            path = url.substring("1m5://".length(), url.indexOf(".1m5"));
            newURL = OneMFiveResources.class.getResource("/web/" + path);
            path = newURL.toString();
            if (!path.endsWith(".html") || !path.endsWith(".htm")) {
                path += "/index.html";
            }
            URLStreamFactoryCustomizer.noProxyForWebKit();
        } else if(url.toLowerCase().endsWith(".i2p")) {
            LOG.info("Using I2P Proxy...");
            path = url;
            URLStreamFactoryCustomizer.useI2PProxyForWebkit();
        } else if(!url.startsWith("http://127.0.0.1")
                && !url.startsWith("http://localhost")
                && !url.startsWith("https://127.0.0.1")
                && !url.startsWith("https://localhost")
                && ManConStatus.MIN_REQUIRED_MANCON != ManCon.NONE) {
            LOG.info("Using TOR Proxy...");
            path = url;
            URLStreamFactoryCustomizer.useTORProxyForWebkit();
        } else {
            LOG.info("No proxy used - localhost or no privacy desired.");
            path = url;
            URLStreamFactoryCustomizer.noProxyForWebKit();
        }
        engine.load(path);
        urlTextField.setText(url);

        LOG.info(path+" loaded");
    }

    public void updateContent(byte[] content, URL url) {
        LOG.info("BrowserView loading content...");
        // TODO: figure out hwo to display content, below does not work.
        String html = new String(content);
        LOG.info(html);
        engine.loadContent(html);
        this.urlTextField.setText(url.toString());
    }

}
