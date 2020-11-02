package io.onemfive.desktop.util.validation;

import io.onemfive.util.Res;

public class IntegerValidator extends InputValidator {

    private int intValue;

    public ValidationResult validate(String input) {
        ValidationResult validationResult = super.validate(input);
        if (!validationResult.isValid)
            return validationResult;

        if (!isInteger(input))
            return new ValidationResult(false, Res.get("validation.notAnInteger"));

        if (isBelowMinValue(intValue))
            return new ValidationResult(false, Res.get("validation.btc.toSmall", Integer.MIN_VALUE));

        if (isAboveMaxValue(intValue))
            return new ValidationResult(false, Res.get("validation.btc.toLarge", Integer.MAX_VALUE));

        return validationResult;
    }

    private boolean isBelowMinValue(int intValue) {
        return intValue < Integer.MIN_VALUE;
    }

    private boolean isAboveMaxValue(int intValue) {
        return intValue > Integer.MAX_VALUE;
    }

    private boolean isInteger(String input) {
        try {
            intValue = Integer.parseInt(input);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
