package com.example.userservice.web.util.annotation;

import com.example.userservice.web.util.validation.PassportValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PassportValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Passport {

    String message() default "Invalid passport number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
