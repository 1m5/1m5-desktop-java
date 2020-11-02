package io.onemfive.desktop.components.overlays;

import de.jensd.fx.fontawesome.AwesomeIcon;
import io.onemfive.desktop.DesktopApp;
import io.onemfive.desktop.MVC;
import io.onemfive.desktop.components.AutoTooltipButton;
import io.onemfive.desktop.components.AutoTooltipCheckBox;
import io.onemfive.desktop.components.AutoTooltipLabel;
import io.onemfive.desktop.components.BusyAnimation;
import io.onemfive.desktop.user.Preferences;
import io.onemfive.desktop.util.FormBuilder;
import io.onemfive.desktop.util.GUIUtil;
import io.onemfive.desktop.views.ViewPath;
import io.onemfive.desktop.views.commons.CommonsView;
import io.onemfive.desktop.views.commons.browser.BrowserView;
import io.onemfive.desktop.views.home.HomeView;
import io.onemfive.desktop.views.support.SupportView;
import ra.util.LanguageUtil;
import ra.util.Resources;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.logging.Logger;

public abstract class Overlay<T extends Overlay<T>> {

    private static final Logger LOG = Logger.getLogger(Overlay.class.getName());

    private enum AnimationType {
        FadeInAtCenter,
        SlideDownFromCenterTop,
        SlideFromRightTop,
        ScaleDownToCenter,
        ScaleFromCenter,
        ScaleYFromCenter
    }

    private enum ChangeBackgroundType {
        BlurLight,
        BlurUltraLight,
        Darken
    }

    protected enum Type {
        Undefined(AnimationType.ScaleFromCenter, ChangeBackgroundType.BlurLight),

        Notification(AnimationType.SlideFromRightTop, ChangeBackgroundType.BlurLight),

        BackgroundInfo(AnimationType.SlideDownFromCenterTop, ChangeBackgroundType.BlurUltraLight),
        Feedback(AnimationType.SlideDownFromCenterTop, ChangeBackgroundType.Darken),

        Information(AnimationType.FadeInAtCenter, ChangeBackgroundType.BlurLight),
        Instruction(AnimationType.ScaleFromCenter, ChangeBackgroundType.BlurLight),
        Attention(AnimationType.ScaleFromCenter, ChangeBackgroundType.BlurLight),
        Confirmation(AnimationType.ScaleYFromCenter, ChangeBackgroundType.BlurLight),

        Warning(AnimationType.ScaleDownToCenter, ChangeBackgroundType.BlurLight),
        Error(AnimationType.ScaleDownToCenter, ChangeBackgroundType.BlurLight);

        public final AnimationType animationType;
        public final ChangeBackgroundType changeBackgroundType;

        Type(AnimationType animationType, ChangeBackgroundType changeBackgroundType) {
            this.animationType = animationType;
            this.changeBackgroundType = changeBackgroundType;
        }
    }

    protected final static double DEFAULT_WIDTH = 668;
    protected Stage stage;
    protected GridPane gridPane;
    protected Pane owner;

    protected int rowIndex = -1;
    protected double width = DEFAULT_WIDTH;
    protected double buttonDistance = 20;

    protected boolean showReportErrorButtons;
    private boolean showBusyAnimation;
    protected boolean hideCloseButton;
    protected boolean isDisplayed;
    protected boolean disableActionButton;

    protected BooleanProperty isHiddenProperty = new SimpleBooleanProperty();

    // Used when a priority queue is used for displaying order of popups. Higher numbers mean lower priority
    protected Integer displayOrderPriority = Integer.MAX_VALUE;

    protected boolean useAnimation = true;

    protected Label headlineIcon, headLineLabel, messageLabel;
    protected String headLine, message, closeButtonText, actionButtonText,
            secondaryActionButtonText, dontShowAgainId, dontShowAgainText,
            truncatedMessage;
    private String headlineStyle;
    protected Button actionButton, secondaryActionButton;
    private HBox buttonBox;
    protected AutoTooltipButton closeButton;

    private HPos buttonAlignment = HPos.RIGHT;

    protected Optional<Runnable> closeHandlerOptional = Optional.<Runnable>empty();
    protected Optional<Runnable> actionHandlerOptional = Optional.empty();
    protected Optional<Runnable> secondaryActionHandlerOptional = Optional.<Runnable>empty();
    protected ChangeListener<Number> positionListener;

    protected Timer centerTime;
    protected Type type = Type.Undefined;

    protected int maxChar = 1800;

    private T cast() {
        //noinspection unchecked
        return (T) this;
    }

    public Overlay() {

    }

    public boolean isIsHiddenProperty() {
        return isHiddenProperty.get();
    }

    public BooleanProperty isHiddenPropertyProperty() {
        return isHiddenProperty;
    }

    public Integer getDisplayOrderPriority() {
        return displayOrderPriority;
    }

    public void setDisplayOrderPriority(Integer displayOrderPriority) {
        this.displayOrderPriority = displayOrderPriority;
    }

    public void show(boolean showAgainChecked) {
        if (dontShowAgainId == null || Preferences.showAgain(dontShowAgainId)) {
            createGridPane();
            if (LanguageUtil.isDefaultLanguageRTL())
                getRootContainer().setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

            addHeadLine();

            if (showBusyAnimation)
                addBusyAnimation();

            addMessage();
            if (showReportErrorButtons)
                addReportErrorButtons();

            addButtons();
            addDontShowAgainCheckBox(showAgainChecked);
            applyStyles();
            onShow();
        }
    }

    public void show() {
        this.show(false);
    }

    protected void onShow() {
    }

    public void hide() {
        if (gridPane != null) {
            animateHide();
        }
        isDisplayed = false;
        isHiddenProperty.set(true);
    }

    protected void animateHide() {
        animateHide(() -> {
            removeEffectFromBackground();

            if (stage != null)
                stage.hide();
            else
                LOG.warning("Stage is null");

            cleanup();
            onHidden();
        });
    }

    protected void onHidden() {
    }

    protected void cleanup() {
        if (centerTime != null)
            centerTime.cancel();

        if (owner == null)
            owner = HomeView.rootContainer;
        Scene rootScene = owner.getScene();
        if (rootScene != null) {
            Window window = rootScene.getWindow();
            if (window != null && positionListener != null) {
                window.xProperty().removeListener(positionListener);
                window.yProperty().removeListener(positionListener);
                window.widthProperty().removeListener(positionListener);
            }
        }
    }

    public T onClose(Runnable closeHandler) {
        this.closeHandlerOptional = Optional.of(closeHandler);
        return cast();
    }

    public T onAction(Runnable actionHandler) {
        this.actionHandlerOptional = Optional.of(actionHandler);
        return cast();
    }

    public T onSecondaryAction(Runnable secondaryActionHandlerOptional) {
        this.secondaryActionHandlerOptional = Optional.of(secondaryActionHandlerOptional);
        return cast();
    }

    public T headLine(String headLine) {
        this.headLine = headLine;
        return cast();
    }

    public T notification(String message) {
        type = Type.Notification;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.notification");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T instruction(String message) {
        type = Type.Instruction;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.instruction");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T attention(String message) {
        type = Type.Attention;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.attention");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T backgroundInfo(String message) {
        type = Type.BackgroundInfo;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.backgroundInfo");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T feedback(String message) {
        type = Type.Feedback;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.feedback");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T confirmation(String message) {
        type = Type.Confirmation;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.confirmation");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T information(String message) {
        type = Type.Information;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.information");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T warning(String message) {
        type = Type.Warning;

        if (headLine == null)
            this.headLine = Resources.get("popup.headline.warning");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T error(String message) {
        type = Type.Error;
        showReportErrorButtons();
        width = 1100;
        if (headLine == null)
            this.headLine = Resources.get("popup.headline.error");
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    @SuppressWarnings("UnusedReturnValue")
    public T showReportErrorButtons() {
        this.showReportErrorButtons = true;
        return cast();
    }

    public T message(String message) {
        this.message = message;
        setTruncatedMessage();
        return cast();
    }

    public T closeButtonText(String closeButtonText) {
        this.closeButtonText = closeButtonText;
        return cast();
    }

    public T useReportBugButton() {
        this.closeButtonText = Resources.get("shared.reportBug");
        this.closeHandlerOptional = Optional.of(() -> MVC.navigation.navigateTo(HomeView.class, SupportView.class));
        return cast();
    }

    public T useIUnderstandButton() {
        this.closeButtonText = Resources.get("shared.iUnderstand");
        return cast();
    }

    public T actionButtonTextWithGoTo(String target) {
        this.actionButtonText = Resources.get("shared.goTo", Resources.get(target));
        return cast();
    }

    public T secondaryActionButtonTextWithGoTo(String target) {
        this.secondaryActionButtonText = Resources.get("shared.goTo", Resources.get(target));
        return cast();
    }

    public T closeButtonTextWithGoTo(String target) {
        this.closeButtonText = Resources.get("shared.goTo", Resources.get(target));
        return cast();
    }

    public T actionButtonText(String actionButtonText) {
        this.actionButtonText = actionButtonText;
        return cast();
    }

    public T secondaryActionButtonText(String secondaryActionButtonText) {
        this.secondaryActionButtonText = secondaryActionButtonText;
        return cast();
    }

    public T useShutDownButton() {
        this.actionButtonText = Resources.get("shared.shutDown");
        this.actionHandlerOptional = Optional.ofNullable(DesktopApp.shutDownHandler);
        return cast();
    }

    public T buttonAlignment(HPos pos) {
        this.buttonAlignment = pos;
        return cast();
    }

    public T width(double width) {
        this.width = width;
        return cast();
    }

    public T maxMessageLength(int maxChar) {
        this.maxChar = maxChar;
        return cast();
    }

    public T showBusyAnimation() {
        this.showBusyAnimation = true;
        return cast();
    }

    public T dontShowAgainId(String key) {
        this.dontShowAgainId = key;
        return cast();
    }

    public T dontShowAgainText(String dontShowAgainText) {
        this.dontShowAgainText = dontShowAgainText;
        return cast();
    }

    public T hideCloseButton() {
        this.hideCloseButton = true;
        return cast();
    }

    public T useAnimation(boolean useAnimation) {
        this.useAnimation = useAnimation;
        return cast();
    }

    public T setHeadlineStyle(String headlineStyle) {
        this.headlineStyle = headlineStyle;
        return cast();
    }

    public T disableActionButton() {
        this.disableActionButton = true;
        return cast();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Protected
    ///////////////////////////////////////////////////////////////////////////////////////////

    protected void createGridPane() {
        gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(64, 64, 64, 64));
        gridPane.setPrefWidth(width);

        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setHalignment(HPos.RIGHT);
        columnConstraints1.setHgrow(Priority.SOMETIMES);
        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(columnConstraints1, columnConstraints2);
    }

    public void display() {
        if (owner == null)
            owner = HomeView.rootContainer;

        if (owner != null) {
            Scene rootScene = owner.getScene();
            if (rootScene != null) {
                Scene scene = new Scene(getRootContainer());
                scene.getStylesheets().setAll(rootScene.getStylesheets());
                scene.setFill(Color.TRANSPARENT);

                setupKeyHandler(scene);

                stage = new Stage();
                stage.setScene(scene);
                Window window = rootScene.getWindow();
                setModality();
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.setOnCloseRequest(event -> {
                    event.consume();
                    doClose();
                });
                stage.sizeToScene();
                stage.show();

                layout();

                addEffectToBackground();

                // On Linux the owner stage does not move the child stage as it does on Mac
                // So we need to apply centerPopup. Further with fast movements the handler loses
                // the latest position, with a delay it fixes that.
                // Also on Mac sometimes the popups are positioned outside of the main app, so keep it for all OS
                positionListener = (observable, oldValue, newValue) -> {
                    if (stage != null) {
                        layout();
                        if (centerTime != null)
                            centerTime.cancel();

                        Platform.runLater(this::layout);
                    }
                };
                window.xProperty().addListener(positionListener);
                window.yProperty().addListener(positionListener);
                window.widthProperty().addListener(positionListener);

                animateDisplay();
                isDisplayed = true;
            }
        }
    }

    protected Region getRootContainer() {
        return gridPane;
    }


    protected void setupKeyHandler(Scene scene) {
        if (!hideCloseButton) {
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                    e.consume();
                    doClose();
                }
            });
        }
    }

    protected void animateDisplay() {
        Region rootContainer = this.getRootContainer();

        rootContainer.setOpacity(0);
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        double duration = getDuration(400);
        Timeline timeline = new Timeline();
        ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

        if (type.animationType == AnimationType.SlideDownFromCenterTop) {
            double startY = -rootContainer.getHeight();
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.translateYProperty(), startY, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.translateYProperty(), -50, interpolator)
            ));
        } else if (type.animationType == AnimationType.ScaleFromCenter) {
            double startScale = 0.25;
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), startScale, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), startScale, interpolator)

            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), 1, interpolator)
            ));
        } else if (type.animationType == AnimationType.ScaleYFromCenter) {
            double startYScale = 0.25;
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), startYScale, interpolator)

            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), 1, interpolator)
            ));
        } else if (type.animationType == AnimationType.ScaleDownToCenter) {
            double startScale = 1.1;
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), startScale, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), startScale, interpolator)

            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), 1, interpolator)
            ));
        } else if (type.animationType == AnimationType.FadeInAtCenter) {
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator)

            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator)
            ));
        }

        timeline.play();
    }

    protected void animateHide(Runnable onFinishedHandler) {
        Interpolator interpolator = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
        double duration = getDuration(200);
        Timeline timeline = new Timeline();
        ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

        Region rootContainer = getRootContainer();
        if (type.animationType == AnimationType.SlideDownFromCenterTop) {
            double endY = -rootContainer.getHeight();
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.translateYProperty(), -10, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.translateYProperty(), endY, interpolator)
            ));

            timeline.setOnFinished(e -> onFinishedHandler.run());
            timeline.play();
        } else if (type.animationType == AnimationType.ScaleFromCenter) {
            double endScale = 0.25;
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), 1, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), endScale, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), endScale, interpolator)
            ));
        } else if (type.animationType == AnimationType.ScaleYFromCenter) {
            rootContainer.setRotationAxis(Rotate.X_AXIS);
            rootContainer.getScene().setCamera(new PerspectiveCamera());
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.rotateProperty(), 0, interpolator),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.rotateProperty(), -90, interpolator),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator)
            ));
        } else if (type.animationType == AnimationType.ScaleDownToCenter) {
            double endScale = 0.1;
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), 1, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), 1, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator),
                    new KeyValue(rootContainer.scaleXProperty(), endScale, interpolator),
                    new KeyValue(rootContainer.scaleYProperty(), endScale, interpolator)
            ));
        } else if (type.animationType == AnimationType.FadeInAtCenter) {
            keyFrames.add(new KeyFrame(Duration.millis(0),
                    new KeyValue(rootContainer.opacityProperty(), 1, interpolator)
            ));
            keyFrames.add(new KeyFrame(Duration.millis(duration),
                    new KeyValue(rootContainer.opacityProperty(), 0, interpolator)
            ));
        }

        timeline.setOnFinished(e -> onFinishedHandler.run());
        timeline.play();
    }

    protected void layout() {
        if (owner == null)
            owner = HomeView.rootContainer;
        Scene rootScene = owner.getScene();
        if (rootScene != null) {
            Window window = rootScene.getWindow();
            double titleBarHeight = window.getHeight() - rootScene.getHeight();
            stage.setX(Math.round(window.getX() + (owner.getWidth() - stage.getWidth()) / 2));

            if (type.animationType == AnimationType.SlideDownFromCenterTop)
                stage.setY(Math.round(window.getY() + titleBarHeight));
            else
                stage.setY(Math.round(window.getY() + titleBarHeight + (owner.getHeight() - stage.getHeight()) / 2));
        }
    }

    protected void addEffectToBackground() {
        if (type.changeBackgroundType == ChangeBackgroundType.BlurUltraLight)
            HomeView.blurUltraLight();
        else if (type.changeBackgroundType == ChangeBackgroundType.BlurLight)
            HomeView.blurLight();
        else
            HomeView.darken();
    }


    protected void applyStyles() {
        Region rootContainer = getRootContainer();
        if (type.animationType == AnimationType.SlideDownFromCenterTop) {
            rootContainer.getStyleClass().add("popup-bg-top");
        } else {
            rootContainer.getStyleClass().add("popup-bg");
        }


        if (headLineLabel != null) {

            switch (type) {
                case Information:
                case BackgroundInfo:
                case Instruction:
                case Confirmation:
                case Feedback:
                case Notification:
                case Attention:
                    headLineLabel.getStyleClass().add("popup-headline-information");
                    headlineIcon.getStyleClass().add("popup-icon-information");
                    headlineIcon.setManaged(true);
                    headlineIcon.setVisible(true);
                    FormBuilder.getIconForLabel(AwesomeIcon.INFO_SIGN, headlineIcon, "1.5em");
                    break;
                case Warning:
                case Error:
                    headLineLabel.getStyleClass().add("popup-headline-warning");
                    headlineIcon.getStyleClass().add("popup-icon-warning");
                    headlineIcon.setManaged(true);
                    headlineIcon.setVisible(true);
                    FormBuilder.getIconForLabel(AwesomeIcon.EXCLAMATION_SIGN, headlineIcon, "1.5em");
                    break;
                default:
                    headLineLabel.getStyleClass().add("popup-headline");
            }
        }
    }

    protected void setModality() {
        stage.initOwner(owner.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
    }

    protected void removeEffectFromBackground() {
        HomeView.removeEffect();
    }

    protected void addHeadLine() {
        if (headLine != null) {
            ++rowIndex;

            HBox hBox = new HBox();
            hBox.setSpacing(7);
            headLineLabel = new AutoTooltipLabel(headLine);
            headlineIcon = new Label();
            headlineIcon.setManaged(false);
            headlineIcon.setVisible(false);
            headlineIcon.setPadding(new Insets(3));
            headLineLabel.setMouseTransparent(true);

            if (headlineStyle != null)
                headLineLabel.setStyle(headlineStyle);

            hBox.getChildren().addAll(headlineIcon, headLineLabel);

            GridPane.setHalignment(hBox, HPos.LEFT);
            GridPane.setRowIndex(hBox, rowIndex);
            GridPane.setColumnSpan(hBox, 2);
            gridPane.getChildren().addAll(hBox);
        }
    }

    protected void addMessage() {
        if (message != null) {
            messageLabel = new AutoTooltipLabel(truncatedMessage);
            messageLabel.setMouseTransparent(true);
            messageLabel.setWrapText(true);
            GridPane.setHalignment(messageLabel, HPos.LEFT);
            GridPane.setHgrow(messageLabel, Priority.ALWAYS);
            GridPane.setMargin(messageLabel, new Insets(3, 0, 0, 0));
            GridPane.setRowIndex(messageLabel, ++rowIndex);
            GridPane.setColumnIndex(messageLabel, 0);
            GridPane.setColumnSpan(messageLabel, 2);
            gridPane.getChildren().add(messageLabel);
        }
    }

    private void addReportErrorButtons() {
        messageLabel.setText(Res.get("popup.reportError", truncatedMessage));

        Button logButton = new AutoTooltipButton(Res.get("popup.reportError.log"));
        GridPane.setMargin(logButton, new Insets(20, 0, 0, 0));
        GridPane.setHalignment(logButton, HPos.LEFT);
        GridPane.setRowIndex(logButton, ++rowIndex);
        gridPane.getChildren().add(logButton);
        logButton.setOnAction(event -> {
            try {
                File logDir = Platform.getInstance().getLogDir();
                File logFile = new File(Paths.get(logDir.getPath(), "desktop.log").toString());
                if(!logFile.exists() && !logFile.createNewFile())
                    throw new IOException("Unable to create log file: "+logFile.getAbsolutePath());
            } catch (IOException e) {
                LOG.warning(e.getLocalizedMessage());
            }
        });

        Button gitHubButton = new AutoTooltipButton(Res.get("popup.reportError.gitHub"));
        GridPane.setHalignment(gitHubButton, HPos.RIGHT);
        GridPane.setRowIndex(gitHubButton, ++rowIndex);
        gridPane.getChildren().add(gitHubButton);
        gitHubButton.setOnAction(event -> {
            if (message != null)
                GUIUtil.copyToClipboard(message);
            MVC.navigation.navigateTo(ViewPath.to(HomeView.class, CommonsView.class, BrowserView.class), "https://github.com/1m5/1m5/issues");
            hide();
        });
    }

    protected void addBusyAnimation() {
        BusyAnimation busyAnimation = new BusyAnimation();
        GridPane.setHalignment(busyAnimation, HPos.CENTER);
        GridPane.setRowIndex(busyAnimation, ++rowIndex);
        GridPane.setColumnSpan(busyAnimation, 2);
        gridPane.getChildren().add(busyAnimation);
    }

    protected void addDontShowAgainCheckBox(boolean isChecked) {
        if (dontShowAgainId != null) {
            // We might have set it and overridden the default, so we check if it is not set
            if (dontShowAgainText == null)
                dontShowAgainText = Res.get("popup.doNotShowAgain");

            CheckBox dontShowAgainCheckBox = new AutoTooltipCheckBox(dontShowAgainText);
            HBox.setHgrow(dontShowAgainCheckBox, Priority.NEVER);
            buttonBox.getChildren().add(0, dontShowAgainCheckBox);

            dontShowAgainCheckBox.setSelected(isChecked);
            Preferences.showAgain(dontShowAgainId, isChecked);
            dontShowAgainCheckBox.setOnAction(e -> Preferences.showAgain(dontShowAgainId, dontShowAgainCheckBox.isSelected()));
        }
    }

    protected void addDontShowAgainCheckBox() {
        this.addDontShowAgainCheckBox(false);
    }

    protected void addButtons() {
        if (!hideCloseButton) {
            closeButton = new AutoTooltipButton(closeButtonText == null ? Res.get("shared.close") : closeButtonText);
            closeButton.getStyleClass().add("compact-button");
            closeButton.setOnAction(event -> doClose());
            closeButton.setMinWidth(70);
            HBox.setHgrow(closeButton, Priority.SOMETIMES);
        }

        Pane spacer = new Pane();

        if (buttonAlignment == HPos.RIGHT) {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
        }

        buttonBox = new HBox();

        GridPane.setHalignment(buttonBox, buttonAlignment);
        GridPane.setRowIndex(buttonBox, ++rowIndex);
        GridPane.setColumnSpan(buttonBox, 2);
        GridPane.setMargin(buttonBox, new Insets(buttonDistance, 0, 0, 0));
        gridPane.getChildren().add(buttonBox);

        if (actionHandlerOptional.isPresent() || actionButtonText != null) {
            actionButton = new AutoTooltipButton(actionButtonText == null ? Res.get("shared.ok") : actionButtonText);

            if (!disableActionButton)
                actionButton.setDefaultButton(true);
            else
                actionButton.setDisable(true);

            HBox.setHgrow(actionButton, Priority.SOMETIMES);

            actionButton.getStyleClass().add("action-button");
            //TODO app wide focus
            //actionButton.requestFocus();

            if (!disableActionButton) {
                actionButton.setOnAction(event -> {
                    hide();
                    actionHandlerOptional.ifPresent(Runnable::run);
                });
            }

            buttonBox.setSpacing(10);

            buttonBox.setAlignment(Pos.CENTER);

            if (buttonAlignment == HPos.RIGHT)
                buttonBox.getChildren().add(spacer);

            buttonBox.getChildren().addAll(actionButton);

            if (secondaryActionButtonText != null && secondaryActionHandlerOptional.isPresent()) {
                secondaryActionButton = new AutoTooltipButton(secondaryActionButtonText);
                secondaryActionButton.setOnAction(event -> {
                    hide();
                    secondaryActionHandlerOptional.ifPresent(Runnable::run);
                });

                buttonBox.getChildren().add(secondaryActionButton);
            }

            if (!hideCloseButton)
                buttonBox.getChildren().add(closeButton);
        } else if (!hideCloseButton) {
            closeButton.setDefaultButton(true);
            buttonBox.getChildren().addAll(spacer, closeButton);
        }
    }

    protected void doClose() {
        hide();
        closeHandlerOptional.ifPresent(Runnable::run);
    }

    protected void setTruncatedMessage() {
        if (message != null && message.length() > maxChar)
            truncatedMessage = StringUtils.abbreviate(message, maxChar);
        else truncatedMessage = Objects.requireNonNullElse(message, "");
    }

    protected double getDuration(double duration) {
        return useAnimation && Preferences.useAnimations ? duration : 1;
    }

    public boolean isDisplayed() {
        return isDisplayed;
    }

    @Override
    public String toString() {
        return "Popup{" +
                "headLine='" + headLine + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
