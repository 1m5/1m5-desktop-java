package io.onemfive.desktop.views.home;

import com.jfoenix.controls.JFXComboBox;
import io.onemfive.desktop.DesktopApp;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.OneMFiveResources;
import io.onemfive.desktop.components.AutoTooltipLabel;
import io.onemfive.desktop.components.AutoTooltipToggleButton;
import io.onemfive.desktop.components.Badge;
import io.onemfive.desktop.util.KeystrokeUtil;
import io.onemfive.desktop.util.Transitions;
import io.onemfive.desktop.views.*;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.community.CommunityView;
import io.onemfive.desktop.views.ops.OpsView;
import io.onemfive.desktop.views.personal.PersonalView;
import io.onemfive.desktop.views.settings.SettingsView;
import io.onemfive.desktop.views.support.SupportView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import onemfive.ManCon;
import onemfive.ManConStatus;
import ra.util.LanguageUtil;
import ra.util.LocaleUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static javafx.scene.layout.AnchorPane.*;

public class HomeView extends InitializableView {

    public static StackPane rootContainer;
    private Label versionLabel;
    private Runnable onUiReadyHandler;
    private final ToggleGroup navButtons = new ToggleGroup();
    private static Transitions transitions = new Transitions();

    private static ImageView neoManConImageView;
    private static ImageView extremeManConImageView;
    private static ImageView veryHighManConImageView;
    private static ImageView highManConImageView;
    private static ImageView mediumManConImageView;
    private static ImageView lowManConImageView;
    private static ImageView noneManConImageView;

    private final ObservableList<ManConComboBoxItem> manConComboBoxItems = FXCollections.observableArrayList();
    private final ObjectProperty<ManConComboBoxItem> selectedManConComboBoxItemProperty = new SimpleObjectProperty<>();

    public HomeView() {}

    public void setTransitions(Transitions t) {
        transitions = t;
    }

    public void setOnUiReadyHandler(Runnable onUiReadyHandler) {
        this.onUiReadyHandler = onUiReadyHandler;
    }

    public StackPane getRootContainer() {
        return rootContainer;
    }

    public static void blurLight() {
        transitions.blur(rootContainer, Transitions.DEFAULT_DURATION, -0.6, false, 5);
    }

    public static void blurUltraLight() {
        transitions.blur(rootContainer, Transitions.DEFAULT_DURATION, -0.6, false, 2);
    }

    public static void darken() {
        transitions.darken(rootContainer, Transitions.DEFAULT_DURATION, false);
    }

    public static void removeEffect() {
        transitions.removeEffect(rootContainer);
    }

    @Override
    protected void initialize() {
        LOG.info("Initializing...");
        rootContainer = (StackPane)root;
        if (LanguageUtil.isDefaultLanguageRTL())
            rootContainer.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        final ToggleButton personalButton = new NavButton(PersonalView.class, OneMFiveResources.get("homeView.menu.personal").toUpperCase());

        final ToggleButton communityButton = new NavButton(CommunityView.class, OneMFiveResources.get("homeView.menu.community").toUpperCase());
//        communityButton.disableProperty().setValue(true);
        final ToggleButton commonsButton = new NavButton(CommonsView.class, OneMFiveResources.get("homeView.menu.commons").toUpperCase());
//        commonsButton.disableProperty().setValue(true);

        final ToggleButton supportButton = new NavButton(SupportView.class, OneMFiveResources.get("homeView.menu.support").toUpperCase());
//        supportButton.disableProperty().setValue(true);
        final ToggleButton settingsButton = new NavButton(SettingsView.class, OneMFiveResources.get("homeView.menu.settings").toUpperCase());
//        settingsButton.disableProperty().setValue(true);
        final ToggleButton opsButton = new NavButton(OpsView.class, OneMFiveResources.get("homeView.menu.ops").toUpperCase());
//        opsButton.disableProperty().setValue(true);

        Badge personalButtonWithBadge = new Badge(personalButton);
        Badge communityButtonWithBadge = new Badge(communityButton);
        Badge commonsButtonWithBadge = new Badge(commonsButton);
        Badge supportButtonWithBadge = new Badge(supportButton);
        Badge settingsButtonWithBadge = new Badge(settingsButton);
        Badge opsButtonWithBadge = new Badge(opsButton);

        DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getNumberInstance(LocaleUtil.currentLocale);
        currencyFormat.setMinimumFractionDigits(0);
        currencyFormat.setMaximumFractionDigits(2);

        root.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.addEventHandler(KeyEvent.KEY_RELEASED, keyEvent -> {

                    if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT1, keyEvent)) {
                        personalButton.fire();
                    } else if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT2, keyEvent)) {
                        communityButton.fire();
                    } else if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT3, keyEvent)) {
                        commonsButton.fire();
                    } else if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT4, keyEvent)) {
                        supportButton.fire();
                    } else if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT5, keyEvent)) {
                        settingsButton.fire();
                    } else if (KeystrokeUtil.isAltOrCtrlPressed(KeyCode.DIGIT6, keyEvent)) {
                        opsButton.fire();
                    }
                });
            }
        });

        // ManCon Combo Box
        ComboBox<ManConComboBoxItem> manConComboBox = new JFXComboBox<>();
        manConComboBox.setVisibleRowCount(6);
        manConComboBox.setFocusTraversable(false);
        manConComboBox.setId("mancon-combo");
        manConComboBox.setPadding(new Insets(0, 0, 0, 0));
        manConComboBox.setCellFactory(p -> getManConComboBoxListCell());
        ListCell<ManConComboBoxItem> buttonCell = getManConComboBoxListCell();
        buttonCell.setId("mancon-combo");
        manConComboBox.setButtonCell(buttonCell);

        VBox manConVBox = new VBox();
        manConVBox.setAlignment(Pos.CENTER);
        manConVBox.setPadding(new Insets(0, 0, 0, 10));
        manConVBox.getChildren().addAll(manConComboBox);
        manConComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedManConComboBoxItemProperty.setValue(newValue);
        });
        selectedManConComboBoxItemProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                manConComboBox.getSelectionModel().select(newValue);
                ManConStatus.MIN_REQUIRED_MANCON = newValue.manConLevel;
                // TODO: Persist this update
                LOG.info("Required ManCon new value: "+newValue.manConLevel.name());
            }
        });
        manConComboBox.setItems(manConComboBoxItems);
        updateManConOptions();
        // TODO: Load min required mancon from a persisted profile setting and then set to context
        manConComboBox.getSelectionModel().select(getComboBoxIndex(ManConStatus.MIN_REQUIRED_MANCON));

        // ManCon Status
        noneManConImageView = getManConImageView(ManCon.NONE, 25);
        VBox noneManConBox = getStatusBox(noneManConImageView, ManCon.NONE);
        lowManConImageView = getManConImageView(ManCon.LOW, 25);
        VBox lowManConBox = getStatusBox(lowManConImageView, ManCon.LOW);
        mediumManConImageView = getManConImageView(ManCon.MEDIUM, 25);
        VBox mediumManConBox = getStatusBox(mediumManConImageView, ManCon.MEDIUM);
        highManConImageView = getManConImageView(ManCon.HIGH, 25);
        VBox highManConBox = getStatusBox(highManConImageView, ManCon.HIGH);
        veryHighManConImageView = getManConImageView(ManCon.VERYHIGH, 25);
        VBox veryHighManConBox = getStatusBox(veryHighManConImageView, ManCon.VERYHIGH);
        extremeManConImageView = getManConImageView(ManCon.EXTREME, 25);
        VBox extremeManConBox = getStatusBox(extremeManConImageView, ManCon.EXTREME);
        neoManConImageView = getManConImageView(ManCon.NEO, 25);
        VBox neoManConBox = getStatusBox(neoManConImageView, ManCon.NEO);
        updateManConBox();

//        HBox primaryNav = new HBox(dashboardButton, getNavigationSeparator(), browserButton, getNavigationSeparator(),
//                emailButtonWithBadge, getNavigationSeparator(), messengerButtonWithBadge, getNavigationSeparator(), calendarButtonWithBadge);

        HBox primaryNav = new HBox(
                personalButton, getNavigationSeparator(),
                communityButton, getNavigationSeparator(),
                commonsButton);

        primaryNav.setAlignment(Pos.CENTER_LEFT);
        primaryNav.getStyleClass().add("nav-primary");
        HBox.setHgrow(primaryNav, Priority.NEVER);

//        HBox secondaryNav = new HBox(voiceButtonWithBadge, getNavigationSpacer(), videoButtonWithBadge,
//                getNavigationSpacer(), appsButtonWithBadge, getNavigationSpacer(), daoButtonWithBadge,
//                getNavigationSeparator(), identitiesButton, getNavigationSeparator(), supportButtonWithBadge,
//                getNavigationSeparator(), settingsButtonWithBadge);
        HBox secondaryNav = new HBox(
                supportButton, getNavigationSeparator(),
                settingsButton, getNavigationSeparator(),
                opsButton);

        secondaryNav.getStyleClass().add("nav-secondary");
        HBox.setHgrow(secondaryNav, Priority.NEVER);
        secondaryNav.setAlignment(Pos.CENTER_LEFT);

        HBox manConStatusBox = new HBox(
                manConVBox, getNavigationSeparator(),
                noneManConBox, getNavigationSeparator(),
                lowManConBox, getNavigationSeparator(),
                mediumManConBox, getNavigationSeparator(),
                highManConBox, getNavigationSeparator(),
                veryHighManConBox, getNavigationSeparator(),
                extremeManConBox, getNavigationSeparator(),
                neoManConBox) {{
            setMaxHeight(41);
            setAlignment(Pos.CENTER_RIGHT);
            setSpacing(9);
            getStyleClass().add("nav-tertiary");
        }};

        HBox navPane = new HBox(primaryNav, secondaryNav, manConStatusBox) {{
            setLeftAnchor(this, 0d);
            setRightAnchor(this, 0d);
            setTopAnchor(this, 0d);
            setPadding(new Insets(0, 0, 0, 0));
            getStyleClass().add("top-navigation");
        }};
        navPane.setAlignment(Pos.CENTER);

        AnchorPane contentContainer = new AnchorPane() {{
            getStyleClass().add("content-pane");
            setLeftAnchor(this, 0d);
            setRightAnchor(this, 0d);
            setTopAnchor(this, 57d);
            setBottomAnchor(this, 0d);
        }};

        AnchorPane applicationContainer = new AnchorPane(navPane, contentContainer) {{
            setId("application-container");
        }};

        BorderPane baseApplicationContainer = new BorderPane(applicationContainer) {{
            setId("base-content-container");
        }};
        baseApplicationContainer.setBottom(createFooter());

//        setupBadge(emailButtonWithBadge, model.getNumUnreadEmails(), model.getShowNumUnreadEmails());
//        setupBadge(emailButtonWithBadge, model.getNumUnreadEmails(), model.getShowNumUnreadEmails());
//        setupBadge(messengerButtonWithBadge, model.getNumUnreadMessages(), model.getShowNumUnreadMessages());
//        setupBadge(calendarButtonWithBadge, model.getNumReminders(), model.getShowNumReminders());
//        setupBadge(callButtonWithBadge, model.getNumVoiceMails(), model.getShowNumVoiceMails());
//        setupBadge(videoButtonWithBadge, model.getNumNewVideos(), model.getShowNumNewVideos());
//        setupBadge(appsButtonWithBadge, model.getNumAppUpdates(), model.getShowNumAppUpdates());
//        setupBadge(daoButtonWithBadge, model.getNumDAONotifications(), model.getShowDAONotifications());
//        setupBadge(supportButtonWithBadge, model.getNumSupportResponses(), model.getShowSupportResponses());
//        setupBadge(settingsButtonWithBadge, model.getNumSettingsNotifications(), model.getShowNumSettingsNotifications());
//        setupBadge(opsButtonWithBadge, model.getNumOpsNotifications(), model.getShowNumOpsNotifications());

        MVC.navigation.addListener(viewPath -> {
            if (viewPath.size() != 2 || viewPath.indexOf(HomeView.class) != 0)
                return;

            Class<? extends View> viewClass = viewPath.tip();
            View view = MVC.loadView(viewClass);
            contentContainer.getChildren().setAll(view.getRoot());

            try {
                navButtons.getToggles().stream()
                        .filter(toggle -> toggle instanceof NavButton)
                        .filter(button -> viewClass == ((NavButton) button).viewClass)
                        .findFirst()
                        .orElseThrow(() -> new Exception("No button matching "+viewClass.getName()+" found"))
                        .setSelected(true);
            } catch (Exception e) {
                LOG.warning(e.getLocalizedMessage());
            }
        });

        VBox splashScreen = createSplashScreen();
        splashScreen.setMinHeight(DesktopApp.HEIGHT);
        splashScreen.setMinWidth(DesktopApp.WIDTH);
        rootContainer.getChildren().addAll(baseApplicationContainer, splashScreen);

//        model.getShowAppScreen().addListener((ov, oldValue, newValue) -> {
//            if (newValue) {
//                navigation.navigateToPreviousVisitedView();
//
//                transitionUtil.fadeOutAndRemove(splashScreen, 1500, actionEvent -> disposeSplashScreen());
                transitions.fadeOutAndRemove(splashScreen, 3500, new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
//                        navPane.setVisible(true);
                        // Default to Personal Dashboard
                        personalButton.fire();
                    }
                });
        transitions.fadeIn(baseApplicationContainer, 3500);
//            }
//        });

        // Delay a bit to give time for rendering the splash screen
//        UserThread.execute(() -> onUiReadyHandler.run());
        LOG.info("Initialized.");
    }

    private Separator getNavigationSeparator() {
        final Separator separator = new Separator(Orientation.VERTICAL);
        HBox.setHgrow(separator, Priority.ALWAYS);
        separator.setMaxHeight(22);
        separator.setMaxWidth(Double.MAX_VALUE);
        return separator;
    }

    private Region getNavigationSpacer() {
        final Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private ListCell<ManConComboBoxItem> getManConComboBoxListCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(ManConComboBoxItem item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getManConImageView(item.manConLevel, 20));
                }
            }
        };
    }

    private ImageView getManConImageView(ManCon manCon, int fitHeight) {
        String imageId;
        switch (manCon) {
            case NEO: imageId = "image-icon-gray";break;
            case EXTREME: imageId = "image-icon-red";break;
            case VERYHIGH: imageId = "image-icon-orange";break;
            case HIGH: imageId = "image-icon-yellow";break;
            case MEDIUM: imageId = "image-icon-blue";break;
            case LOW: imageId = "image-icon-green";break;
            default: imageId = "image-icon-white";
        }
        ImageView iv = new ImageView();
        iv.setFitHeight(fitHeight);
        iv.setPreserveRatio(true);
        iv.setId(imageId);
        iv.setCache(true);
        iv.setCacheHint(CacheHint.SPEED);
        return iv;
    }

    private VBox getStatusBox(ImageView imageView, ManCon manCon) {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(imageView);
        return vBox;
    }

//    private Tuple2<ImageView, VBox> getStatusBox(URL statusImageURL, int fitHeight) {
//        ImageView statusImageView = new ImageView(new Image(statusImageURL.toString()));
//        statusImageView.setFitHeight(fitHeight);
//        statusImageView.setPreserveRatio(true);
//
//        VBox vBox = new VBox();
//        vBox.setAlignment(Pos.CENTER_LEFT);
//        vBox.getChildren().addAll(statusImageView);
//        return new Tuple2<>(statusImageView, vBox);
//    }

//    private String getPriceProvider() {
//        return model.getIsFiatCurrencyPriceFeedSelected().get() ? "BitcoinAverage" : "Poloniex";
//    }
//
//
//    private String getPriceProviderTooltipString() {
//
//        String res;
//        if (model.getIsFiatCurrencyPriceFeedSelected().get()) {
//            res = Res.get("mainView.marketPrice.tooltip",
//                    "https://bitcoinaverage.com",
//                    "",
//                    formatter.formatTime(model.getPriceFeedService().getLastRequestTimeStampBtcAverage()),
//                    model.getPriceFeedService().getProviderNodeAddress());
//        } else {
//            String altcoinExtra = "\n" + Res.get("mainView.marketPrice.tooltip.altcoinExtra");
//            res = Res.get("mainView.marketPrice.tooltip",
//                    "https://poloniex.com",
//                    altcoinExtra,
//                    formatter.formatTime(model.getPriceFeedService().getLastRequestTimeStampPoloniex()),
//                    model.getPriceFeedService().getProviderNodeAddress());
//        }
//        return res;
//    }

    private VBox createSplashScreen() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setId("splash");

        ImageView logo = new ImageView();
        logo.setId("image-splash-logo");
        vBox.getChildren().add(logo);
        return vBox;
    }

    private void disposeSplashScreen() {
//        model.getWalletServiceErrorMsg().removeListener(walletServiceErrorMsgListener);
//        model.getBtcSplashSyncIconId().removeListener(btcSyncIconIdListener);
//
//        model.getP2pNetworkWarnMsg().removeListener(splashP2PNetworkErrorMsgListener);
//        model.getP2PNetworkIconId().removeListener(splashP2PNetworkIconIdListener);
//        model.getSplashP2PNetworkAnimationVisible().removeListener(splashP2PNetworkVisibleListener);
//
//        btcSplashInfo.textProperty().unbind();
//        btcSyncIndicator.progressProperty().unbind();
//
//        splashP2PNetworkLabel.textProperty().unbind();
//
//        model.onSplashScreenRemoved();
    }

    private AnchorPane createFooter() {
        // line
        Separator separator = new Separator();
        separator.setId("footer-pane-line");
        separator.setPrefHeight(1);
        setLeftAnchor(separator, 0d);
        setRightAnchor(separator, 0d);
        setTopAnchor(separator, 0d);

        // BTC
//        Label btcInfoLabel = new AutoTooltipLabel();
//        btcInfoLabel.setId("footer-pane");
//        btcInfoLabel.textProperty().bind(model.getBtcInfo());
//
//        ProgressBar blockchainSyncIndicator = new JFXProgressBar(-1);
//        blockchainSyncIndicator.setPrefWidth(80);
//        blockchainSyncIndicator.setMaxHeight(10);
//        blockchainSyncIndicator.progressProperty().bind(model.getCombinedSyncProgress());
//
//        model.getWalletServiceErrorMsg().addListener((ov, oldValue, newValue) -> {
//            if (newValue != null) {
//                btcInfoLabel.setId("splash-error-state-msg");
//                btcInfoLabel.getStyleClass().add("error-text");
//                if (btcNetworkWarnMsgPopup == null) {
//                    btcNetworkWarnMsgPopup = new Popup<>().warning(newValue);
//                    btcNetworkWarnMsgPopup.show();
//                }
//            } else {
//                btcInfoLabel.setId("footer-pane");
//                if (btcNetworkWarnMsgPopup != null)
//                    btcNetworkWarnMsgPopup.hide();
//            }green
//        });
//
//        model.getCombinedSyncProgress().addListener((ov, oldValue, newValue) -> {
//            if ((double) newValue >= 1) {
//                blockchainSyncIndicator.setVisible(false);
//                blockchainSyncIndicator.setManaged(false);
//            }
//        });

//        HBox blockchainSyncBox = new HBox();
//        blockchainSyncBox.setSpacing(10);
//        blockchainSyncBox.setAlignment(Pos.CENTER);
//        blockchainSyncBox.getChildren().addAll(btcInfoLabel, blockchainSyncIndicator);
//        setLeftAnchor(blockchainSyncBox, 10d);
//        setBottomAnchor(blockchainSyncBox, 7d);

        // version
        String version = System.getProperty("1m5.version");
        versionLabel = new AutoTooltipLabel();
        versionLabel.setId("footer-pane");
        versionLabel.setTextAlignment(TextAlignment.CENTER);
        versionLabel.setAlignment(Pos.BASELINE_RIGHT);
        versionLabel.setPadding(new Insets(0,0,0,10));
        versionLabel.setText("v" + version);
//        rootContainer.widthProperty().addListener((ov, oldValue, newValue) -> {
//            versionLabel.setLayoutX(((double) newValue - versionLabel.getWidth()) / 2);
//        });
        setBottomAnchor(versionLabel, 7d);
//        model.getNewVersionAvailableProperty().addListener((observable, oldValue, newValue) -> {
//            versionLabel.getStyleClass().removeAll("version-new", "version");
//            if (newValue) {
//                versionLabel.getStyleClass().add("version-new");
//                versionLabel.setOnMouseClicked(e -> model.onOpenDownloadWindow());
//                versionLabel.setText("v" +version+ " " + Resources.get("homeView.version.update"));
//            } else {
//                versionLabel.getStyleClass().add("version");
//                versionLabel.setOnMouseClicked(null);
//                versionLabel.setText("v" + version;
//            }
//        });

        // P2P Networks
//        Label p2PNetworkLabel = new AutoTooltipLabel();
//        p2PNetworkLabel.setId("footer-pane");
//        p2PNetworkLabel.textProperty().bind(model.getP2PNetworkInfo());
//
//        ImageView p2PNetworkIcon = new ImageView();
//        setRightAnchor(p2PNetworkIcon, 10d);
//        setBottomAnchor(p2PNetworkIcon, 5d);
//        p2PNetworkIcon.setOpacity(0.4);
//        p2PNetworkIcon.idProperty().bind(model.getP2PNetworkIconId());
//        p2PNetworkLabel.idProperty().bind(model.getP2pNetworkLabelId());
//        model.getP2pNetworkWarnMsg().addListener((ov, oldValue, newValue) -> {
//            if (newValue != null) {
//                p2PNetworkWarnMsgPopup = new Popup<>().warning(newValue);
//                p2PNetworkWarnMsgPopup.show();
//            } else if (p2PNetworkWarnMsgPopup != null) {
//                p2PNetworkWarnMsgPopup.hide();
//            }
//        });
//
//        model.getUpdatedDataReceived().addListener((observable, oldValue, newValue) -> {
//            p2PNetworkIcon.setOpacity(1);
//            p2pNetworkProgressBar.setProgress(0);
//        });
//
//        p2pNetworkProgressBar = new JFXProgressBar(-1);
//        p2pNetworkProgressBar.setMaxHeight(2);
//        p2pNetworkProgressBar.prefWidthProperty().bind(p2PNetworkLabel.widthProperty());
//
//        VBox vBox = new VBox();
//        vBox.setAlignment(Pos.CENTER_RIGHT);
//        vBox.getChildren().addAll(p2PNetworkLabel, p2pNetworkProgressBar);
//        setRightAnchor(vBox, 33d);
//        setBottomAnchor(vBox, 5d);

//        return new AnchorPane(separator, blockchainSyncBox, versionLabel, vBox, p2PNetworkIcon) {{
//            setId("footer-pane");
//            setMinHeight(30);
//            setMaxHeight(30);
//        }};

        // Tor Network
        Label torNetworkLabel = new AutoTooltipLabel();
        torNetworkLabel.setId("footer-pane");

        // I2P Network
        Label i2pNetworkLabel = new AutoTooltipLabel();
        i2pNetworkLabel.setId("footer-pane");

        // WiFi-Direct Network
        Label wifiDirectNetworkLabel = new AutoTooltipLabel();
        wifiDirectNetworkLabel.setId("footer-pane");

        // Bluetooth Network
        Label bluetoothNetworkLabel = new AutoTooltipLabel();
        bluetoothNetworkLabel.setId("footer-pane");

        // Satellite Network
        Label satelliteNetworkLabel = new AutoTooltipLabel();
        satelliteNetworkLabel.setId("footer-pane");

        // Full Spectrum Radio Network
        Label fsRadioNetworkLabel = new AutoTooltipLabel();
        fsRadioNetworkLabel.setId("footer-pane");

        // LiFi Network
        Label lifiNetworkLabel = new AutoTooltipLabel();
        lifiNetworkLabel.setId("footer-pane");

        return new AnchorPane(separator, versionLabel) {{
            setId("footer-pane");
            setMinHeight(30);
            setMaxHeight(30);
        }};
    }

    private void setupBadge(Badge buttonWithBadge, StringProperty badgeNumber, BooleanProperty badgeEnabled) {
        buttonWithBadge.textProperty().bind(badgeNumber);
        buttonWithBadge.setEnabled(badgeEnabled.get());
        badgeEnabled.addListener((observable, oldValue, newValue) -> {
            buttonWithBadge.setEnabled(newValue);
            buttonWithBadge.refreshBadge();
        });

        buttonWithBadge.setPosition(Pos.TOP_RIGHT);
        buttonWithBadge.setMinHeight(34);
        buttonWithBadge.setMaxHeight(34);
    }

    private class NavButton extends AutoTooltipToggleButton {

        private final Class<? extends View> viewClass;

        NavButton(Class<? extends View> viewClass, String title) {
            super(title);

            this.viewClass = viewClass;

            this.setToggleGroup(navButtons);
            this.getStyleClass().add("nav-button");
            // Japanese fonts are dense, increase top nav button text size
//            if (model.getPreferences().getUserLanguage().equals("ja")) {
//                this.getStyleClass().add("nav-button-japanese");
//            }

            this.selectedProperty().addListener((ov, oldValue, newValue) -> this.setMouseTransparent(newValue));

            this.setOnAction(e -> MVC.navigation.navigateTo(HomeView.class, viewClass));
        }

    }

    private class ManConComboBoxItem {
        public final ManCon manConLevel;
        public final ImageView manConImageView;

        public ManConComboBoxItem(ManCon manConLevel) {
            this.manConLevel = manConLevel;
            this.manConImageView = new ImageView(new Image(OneMFiveResources.getManConIcon(manConLevel).toString()));
        }

    }

    private void updateManConOptions() {
        manConComboBoxItems.clear();
        for(int start = 6; start >= ManConStatus.MAX_SUPPORTED_MANCON.ordinal(); start--) {
            manConComboBoxItems.add(new ManConComboBoxItem(ManCon.fromOrdinal(start)));
        }
    }

    private int getComboBoxIndex(ManCon manCon) {
        int index = 0;
        for(HomeView.ManConComboBoxItem i : manConComboBoxItems) {
            if(i.manConLevel == manCon)
                return index;
            index++;
        }
        return index;
    }

    public void updateManConBox() {
        ColorAdjust smoke = new ColorAdjust();
        smoke.setBrightness(-0.6);
        Glow glow = new Glow(0.6);
        switch(ManConStatus.MAX_AVAILABLE_MANCON) {
            case NEO: {
                neoManConImageView.setEffect(glow);
                extremeManConImageView.setEffect(glow);
                veryHighManConImageView.setEffect(glow);
                highManConImageView.setEffect(glow);
                mediumManConImageView.setEffect(glow);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } case EXTREME: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(glow);
                veryHighManConImageView.setEffect(glow);
                highManConImageView.setEffect(glow);
                mediumManConImageView.setEffect(glow);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } case VERYHIGH: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(smoke);
                veryHighManConImageView.setEffect(glow);
                highManConImageView.setEffect(glow);
                mediumManConImageView.setEffect(glow);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } case HIGH: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(smoke);
                veryHighManConImageView.setEffect(smoke);
                highManConImageView.setEffect(glow);
                mediumManConImageView.setEffect(glow);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } case MEDIUM: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(smoke);
                veryHighManConImageView.setEffect(smoke);
                highManConImageView.setEffect(smoke);
                mediumManConImageView.setEffect(glow);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } case LOW: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(smoke);
                veryHighManConImageView.setEffect(smoke);
                highManConImageView.setEffect(smoke);
                mediumManConImageView.setEffect(smoke);
                lowManConImageView.setEffect(glow);
                noneManConImageView.setEffect(glow);
                break;
            } default: {
                neoManConImageView.setEffect(smoke);
                extremeManConImageView.setEffect(smoke);
                veryHighManConImageView.setEffect(smoke);
                highManConImageView.setEffect(smoke);
                mediumManConImageView.setEffect(smoke);
                lowManConImageView.setEffect(smoke);
                noneManConImageView.setEffect(glow);
            }
        }
    }

}
