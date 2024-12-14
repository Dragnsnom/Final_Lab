package com.example.userservice.web.util.annotation;

import com.example.userservice.web.util.validation.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    String message() default "Invalid passport number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
