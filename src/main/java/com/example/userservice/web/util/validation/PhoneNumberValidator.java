package com.example.userservice.web.util.validation;

import com.example.userservice.web.util.annotation.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final String REGEX_PHONE_NUMBER = "\\+?7(\\d){10}";

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        return phoneNumber.matches(REGEX_PHONE_NUMBER);
    }
}
