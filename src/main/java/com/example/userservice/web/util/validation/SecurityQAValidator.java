package com.example.userservice.web.util.validation;

import com.example.userservice.web.util.annotation.SecurityQA;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SecurityQAValidator implements ConstraintValidator<SecurityQA, String> {
    public static final String SECURITY_QUESTION_ANSWER_PATTERN = "^[А-Яа-я\\w\\s!?,._-]+$";

    @Override
    public boolean isValid(String QA, ConstraintValidatorContext constraintValidatorContext) {
        return QA.matches(SECURITY_QUESTION_ANSWER_PATTERN);
    }
}
