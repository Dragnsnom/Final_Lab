package com.example.userservice.web.util.validation;


import com.example.userservice.web.util.annotation.PlaceOfIssue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlaceOfIssueValidator implements ConstraintValidator<PlaceOfIssue, String> {

    @Override
    public boolean isValid(String placeOfIssue, ConstraintValidatorContext constraintValidatorContext) {
        return placeOfIssue.matches(PlaceOfIssue.REGEX);
    }
}