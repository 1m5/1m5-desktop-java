package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.util.logging.Logger;

public class TextFieldWithIcon extends AnchorPane {

    public static final Logger LOG = Logger.getLogger(TextFieldWithIcon.class.getName());

    private final Label iconLabel;
    private final TextField textField;
    private final Label dummyTextField;

    public TextFieldWithIcon() {
        textField = new JFXTextField();
        textField.setEditable(false);
        textField.setMouseTransparent(true);
        textField.setFocusTraversable(false);
        setLeftAnchor(textField, 0d);
        setRightAnchor(textField, 0d);

        dummyTextField = new Label();
        dummyTextField.setWrapText(true);
        dummyTextField.setAlignment(Pos.CENTER_LEFT);
        dummyTextField.setTextAlignment(TextAlignment.LEFT);
        dummyTextField.setMouseTransparent(true);
        dummyTextField.setFocusTraversable(false);
        setLeftAnchor(dummyTextField, 0d);
        dummyTextField.setVisible(false);

        iconLabel = new Label();
        iconLabel.setLayoutX(0);
        iconLabel.setLayoutY(3);

        dummyTextField.widthProperty().addListener((observable, oldValue, newValue) -> {
            iconLabel.setLayoutX(dummyTextField.widthProperty().get() + 20);
        });

        getChildren().addAll(textField, dummyTextField, iconLabel);
    }



    public void setIcon(AwesomeIcon iconLabel) {
        AwesomeDude.setIcon(this.iconLabel, iconLabel);
    }

    public void setText(String text) {
        textField.setText(text);
        dummyTextField.setText(text);
    }
}
