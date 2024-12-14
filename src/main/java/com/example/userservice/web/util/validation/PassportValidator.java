package com.example.userservice.web.util.validation;

import com.example.userservice.web.util.annotation.Passport;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PassportValidator  implements ConstraintValidator<Passport, String> {

    private static final String PASSPORT_NUMBER_REGEX_PATTERN =
            "([A-Z0-9]*\\d+[A-Z0-9]*(\s|-)?[A-Z0-9]+)|[A-Z]+(\s|-)?[A-Z0-9]*\\d+[A-Z0-9]*";

    @Override
    public boolean isValid(String passportNumber, ConstraintValidatorContext constraintValidatorContext) {
        return passportNumber.matches(PASSPORT_NUMBER_REGEX_PATTERN);
    }
}
