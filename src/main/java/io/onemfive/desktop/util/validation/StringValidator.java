package io.onemfive.desktop.util.validation;

import io.onemfive.util.Res;

public class StringValidator extends InputValidator {

    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid)
            return validationResult;

        int length = 0;
        if (!isStringWithFixedLength(input, length))
            return new ValidationResult(false, Res.get("validation.invalidInput", input));

        return validationResult;
    }
}
