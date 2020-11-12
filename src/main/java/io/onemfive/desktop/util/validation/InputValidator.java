package io.onemfive.desktop.util.validation;

import ra.util.Resources;

import java.math.BigInteger;

public class InputValidator {

    public ValidationResult validate(String input) {
        return validateIfNotEmpty(input);
    }

    protected ValidationResult validateIfNotEmpty(String input) {
        //trim added to avoid empty input
        if (input == null || input.trim().length() == 0)
            return new ValidationResult(false, Resources.get("validation.empty"));
        else
            return new ValidationResult(true);
    }

    public static class ValidationResult {
        public final boolean isValid;
        public final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public ValidationResult(boolean isValid) {
            this(isValid, null);
        }

        public ValidationResult and(ValidationResult next) {
            if (this.isValid)
                return next;
            else
                return this;
        }

        @Override
        public String toString() {
            return "validationResult {" +
                    "isValid=" + isValid +
                    ", errorMessage='" + errorMessage + '\'' +
                    '}';
        }
    }

    protected boolean isPositiveNumber(String input) {
        try {
            return input != null && new BigInteger(input).compareTo(BigInteger.ZERO) >= 0;
        } catch (Throwable t) {
            return false;
        }
    }

    protected boolean isNumberWithFixedLength(String input, int length) {
        return isPositiveNumber(input) && input.length() == length;
    }

    protected boolean isNumberInRange(String input, int minLength, int maxLength) {
        return isPositiveNumber(input) && input.length() >= minLength && input.length() <= maxLength;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean isStringWithFixedLength(String input, int length) {
        return input != null && input.length() == length;
    }

    protected boolean isStringInRange(String input, int minLength, int maxLength) {
        return input != null && input.length() >= minLength && input.length() <= maxLength;
    }
}
