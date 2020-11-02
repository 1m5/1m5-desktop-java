package io.onemfive.desktop.util.validation;

import io.onemfive.util.Res;

import java.net.URL;

public class UrlInputValidator extends InputValidator {

    public UrlInputValidator() {
    }

    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid)
            return validationResult;

        try {
            new URL(input); // does not cover all invalid urls, so we use a regex as well
            String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            if(!input.matches(regex)) {
                validationResult = new ValidationResult(false, Res.get("validation.invalidURL"));
            };
            return validationResult;
        } catch (Throwable t) {
            return new ValidationResult(false, Res.get("validation.invalidUrl"));
        }
    }
}
