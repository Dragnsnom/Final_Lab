package com.example.userservice.web.util.annotation;


import com.example.userservice.web.util.validation.PlaceOfIssueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PlaceOfIssueValidator.class)
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PlaceOfIssue {

    String REGEX = "[a-zA-Z]+\s+[a-zA-Z]+||[а-яА-Я]+\s+[а-яА-Я]+";

    String DEFAULT_MESSAGE = "The place where your document is issued must contain at least 2 words";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
