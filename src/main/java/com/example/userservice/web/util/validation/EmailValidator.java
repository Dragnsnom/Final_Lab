package com.example.userservice.web.util.validation;

import com.example.userservice.web.util.annotation.Email;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;

public class EmailValidator implements ConstraintValidator<Email, String> {
    public static final String EMAIL_REGEX_PATTERN = "^(?=.{5,30}@)[A-Za-z0-9_]+([A-Za-z0-9_-]+)*(\\.[A-Za-z0-9_-]+)*" +
            "@[^-]([A-Za-z0-9_-]+)(\\.[A-Za-z]+)*(\\.[A-Za-z]{2,3})$";

    public static final String EMAIL_AFTER_AT_REGEX = "^[a-zA-Z]+(\\.[a-zA-Z]+)*$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (!email.matches(EMAIL_REGEX_PATTERN)){
            return false;
        }
        String [] mas = email.split("@");
        String afterAt = mas[1];
        String beforeAt = mas[0];

        if (beforeAt.equalsIgnoreCase("admin")
                || (afterAt.length() > 19 || afterAt.length() < 5)
                || !afterAt.matches(EMAIL_AFTER_AT_REGEX)) {
            return false;
        }
        return true;
    }
}
