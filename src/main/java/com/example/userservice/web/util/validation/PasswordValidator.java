package com.example.userservice.web.util.validation;

import com.example.userservice.web.util.annotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return password.matches(Password.REGEX);
    }
}