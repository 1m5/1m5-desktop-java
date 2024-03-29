package io.onemfive.desktop.util.validation;

import com.jfoenix.validation.base.ValidatorBase;

public class JFXInputValidator extends ValidatorBase {

    public JFXInputValidator() {
        super();
    }

    @Override
    protected void eval() {
        //Do nothing as validation is handled by current validation logic
    }

    public void resetValidation() {
        hasErrors.set(false);
    }

    public void applyErrorMessage(InputValidator.ValidationResult newValue) {
        message.set(newValue.errorMessage);
        hasErrors.set(true);
    }
}
