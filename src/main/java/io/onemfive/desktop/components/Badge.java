package io.onemfive.desktop.components;

import javafx.animation.FadeTransition;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

@DefaultProperty(value = "control")
public class Badge extends StackPane {

    private static final String DEFAULT_STYLE_CLASS = "jfx-badge";

    private Group badge;
    private SimpleStringProperty text = new SimpleStringProperty();

    protected Node control;
    protected ObjectProperty<Pos> position = new SimpleObjectProperty<>();

    private Boolean enabled = true;

    public Badge() {
        this(null);
    }

    public Badge(Node control) {
        this(control, Pos.TOP_RIGHT);
    }

    public Badge(Node control, Pos pos) {
        initialize();
        setPosition(pos);
        setControl(control);
        position.addListener((o, oldVal, newVal) -> StackPane.setAlignment(badge, newVal));
    }

    public final String getText() {
        return text.get();
    }

    public final void setText(String value) {
        text.set(value);
    }

    public final StringProperty textProperty() {
        return text;
    }

    public void setControl(Node control) {
        if (control != null) {
            this.control = control;
            this.badge = new Group();
            this.getChildren().add(control);
            this.getChildren().add(badge);

            if (control instanceof Region) {
                ((Region) control).widthProperty().addListener((o, oldVal, newVal) -> refreshBadge());
                ((Region) control).heightProperty().addListener((o, oldVal, newVal) -> refreshBadge());
            }
            text.addListener((o, oldVal, newVal) -> refreshBadge());
        }
    }

    public Node getControl() {
        return this.control;
    }

    public Pos getPosition() {
        return position == null ? Pos.TOP_RIGHT : position.get();
    }

    public ObjectProperty<Pos> positionProperty() {
        return this.position;
    }

    public void setPosition(Pos position) {
        this.position.set(position);
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void refreshBadge() {
        badge.getChildren().clear();
        if (enabled) {
            Label labelControl = new Label(text.getValue());
            StackPane badgePane = new StackPane();
            badgePane.getStyleClass().add("badge-pane");
            badgePane.getChildren().add(labelControl);
            badge.getChildren().add(badgePane);
            StackPane.setAlignment(badge, getPosition());
            FadeTransition ft = new FadeTransition(Duration.millis(666), badge);
            ft.setFromValue(0);
            ft.setToValue(1.0);
            ft.setCycleCount(1);
            ft.setAutoReverse(true);
            ft.play();
        }
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

}
