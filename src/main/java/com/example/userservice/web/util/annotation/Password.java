package com.example.userservice.web.util.annotation;

import com.example.userservice.web.util.validation.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Password {

    /**
     * The password mask must be between 6 and 20 characters long and contain at least 3 of the 4 allowed characters:
     * 1. Capital letters A-Z
     * 2. Lowercase letters a-z
     * 3. Special symbols  # $ % & ' ( ) * + , - . / : ; < = > ? @ [ \ ] ^ _` { | } ~
     * 4. Digits 0-9
     */
    String REGEX = "^(?:(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])|(?=.*\\d)(?=.*[a-z])" +
            "(?=.*[\\W])|(?=.*\\d)(?=.*[A-Z])" +
            "(?=.*[\\W])|(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]))[\\da-zA-Z\\W]{6,20}$";

    String DEFAULT_MESSAGE = "The password must be no shorter than 6 characters " +
            "and no longer than 20 characters and contain only allowed characters";

    String message() default DEFAULT_MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}