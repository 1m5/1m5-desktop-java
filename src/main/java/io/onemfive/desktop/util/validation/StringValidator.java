package io.onemfive.desktop.util.validation;

import ra.common.Resources;

public class StringValidator extends InputValidator {

    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid)
            return validationResult;

        int length = 0;
        if (!isStringWithFixedLength(input, length))
            return new ValidationResult(false, Resources.get("validation.invalidInput", input));

        return validationResult;
    }
}
