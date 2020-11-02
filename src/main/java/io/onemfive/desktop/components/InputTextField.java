package io.onemfive.desktop.components;

import com.jfoenix.controls.JFXTextField;
import io.onemfive.desktop.util.validation.InputValidator;
import io.onemfive.desktop.util.validation.JFXInputValidator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;

/**
 * TextField with validation support.
 * If validator is set it supports on focus out validation with that validator. If a more sophisticated validation is
 * needed the validationResultProperty can be used for applying validation result done by external validation.
 * In case the isValid property in validationResultProperty get set to false we display a red border and an error
 * message within the errorMessageDisplay placed on the right of the text field.
 * The errorMessageDisplay gets closed when the ValidatingTextField instance gets removed from the scene graph or when
 * hideErrorMessageDisplay() is called.
 * There can be only 1 errorMessageDisplays at a time we use static field for it.
 * The position is derived from the position of the textField itself or if set from the layoutReference node.
 */
//TODO There are some rare situation where it behaves buggy. Needs further investigation and improvements.
public class InputTextField extends JFXTextField {

    private final ObjectProperty<InputValidator.ValidationResult> validationResult = new SimpleObjectProperty<>
            (new InputValidator.ValidationResult(true));

    private final JFXInputValidator jfxValidationWrapper = new JFXInputValidator();
    private double inputLineExtension = 0;

    private InputValidator validator;


    public InputValidator getValidator() {
        return validator;
    }

    public void setValidator(InputValidator validator) {
        this.validator = validator;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////

    public InputTextField() {
        super();

        getValidators().add(jfxValidationWrapper);

        validationResult.addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isValid) {
                    resetValidation();
                } else {
                    resetValidation();
                    validate();

                    jfxValidationWrapper.applyErrorMessage(newValue);
                }
                validate();
            }
        });

        focusedProperty().addListener((o, oldValue, newValue) -> {
            if (oldValue && !newValue && validator != null) {
                this.validationResult.set(validator.validate(getText()));
            } else if (!oldValue && newValue && validator != null) {
                this.validationResult.set(new InputValidator.ValidationResult(true));
            }
        });
    }

    public InputTextField(double inputLineExtension) {
        this();
        this.inputLineExtension = inputLineExtension;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void resetValidation() {
        jfxValidationWrapper.resetValidation();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////////////////////

    public ObjectProperty<InputValidator.ValidationResult> validationResultProperty() {
        return validationResult;
    }

    protected Skin<?> createDefaultSkin() {
        return new JFXTextFieldSkin<>(this, inputLineExtension);
    }
}
