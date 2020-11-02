package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import io.onemfive.desktop.components.controlsfx.control.PopOver;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.logging.Logger;

import static io.onemfive.desktop.util.FormBuilder.getIcon;
import static io.onemfive.desktop.util.FormBuilder.getRegularIconForLabel;

public class InfoTextField extends AnchorPane {

    public static final Logger LOG = Logger.getLogger(InfoTextField.class.getName());

    protected final JFXTextField textField;

    private final StringProperty text = new SimpleStringProperty();
    protected final Label infoIcon;
    private Label currentIcon;
    private PopOverWrapper popoverWrapper = new PopOverWrapper();
    private PopOver.ArrowLocation arrowLocation;

    public InfoTextField() {

        arrowLocation = PopOver.ArrowLocation.RIGHT_TOP;
        textField = new JFXTextField();
        textField.setLabelFloat(true);
        textField.setEditable(false);
        textField.textProperty().bind(text);
        textField.setFocusTraversable(false);
        textField.setId("info-field");

        infoIcon = getIcon(AwesomeIcon.INFO_SIGN);
        infoIcon.setLayoutY(5);
        infoIcon.getStyleClass().addAll("icon", "info");

        AnchorPane.setRightAnchor(infoIcon, 7.0);
        AnchorPane.setRightAnchor(textField, 0.0);
        AnchorPane.setLeftAnchor(textField, 0.0);

        hideIcons();

        getChildren().addAll(textField, infoIcon);
    }

    public JFXTextField getTextField() {
        return textField;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void setContentForInfoPopOver(Node node) {

        currentIcon = infoIcon;

        hideIcons();
        setActionHandlers(node);
    }

    public void setContent(MaterialDesignIcon icon, String info, String style, double opacity) {
        hideIcons();

        currentIcon = new Label();
        Text textIcon = getRegularIconForLabel(icon, currentIcon);

        setActionHandlers(new Label(info));

        currentIcon.setLayoutY(5);
        textIcon.getStyleClass().addAll("icon", style);
        currentIcon.setOpacity(opacity);
        AnchorPane.setRightAnchor(currentIcon, 7.0);

        getChildren().add(currentIcon);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Private
    ///////////////////////////////////////////////////////////////////////////////////////////
    private void hideIcons() {
        infoIcon.setManaged(false);
        infoIcon.setVisible(false);
    }

    private void setActionHandlers(Node node) {

        currentIcon.setManaged(true);
        currentIcon.setVisible(true);

        // As we don't use binding here we need to recreate it on mouse over to reflect the current state
        currentIcon.setOnMouseEntered(e -> popoverWrapper.showPopOver(() -> createPopOver(node)));
        currentIcon.setOnMouseExited(e -> popoverWrapper.hidePopOver());
    }

    private PopOver createPopOver(Node node) {
        node.getStyleClass().add("default-text");

        PopOver popover = new PopOver(node);
        if (currentIcon.getScene() != null) {
            popover.setDetachable(false);
            popover.setArrowLocation(arrowLocation);
            popover.setArrowIndent(5);

            popover.show(currentIcon, -17);
        }
        return popover;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters/Setters
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void setText(String text) {
        this.text.set(text);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }
}
