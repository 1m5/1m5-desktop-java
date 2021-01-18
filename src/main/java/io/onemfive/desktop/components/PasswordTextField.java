package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXPasswordField;
import javafx.scene.control.Skin;

public class PasswordTextField extends JFXPasswordField {

    public PasswordTextField() {
        super();
        setLabelFloat(true);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXTextFieldSkin<>(this, 0);
    }
}
