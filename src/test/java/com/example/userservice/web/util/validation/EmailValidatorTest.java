package com.example.userservice.web.util.validation;

import org.junit.jupiter.api.Test;

import static com.example.userservice.web.util.validation.EmailValidator.EMAIL_REGEX_PATTERN;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailValidatorTest {
    private String email;

    @Test
    void isValid_whenEmailValid_thenReturnTrue() {
        email="username@domain.com";
        assertTrue(email.matches(EMAIL_REGEX_PATTERN));

        email="user.name@domain.com";
        assertTrue(email.matches(EMAIL_REGEX_PATTERN));

        email="user-name@domain.com";
        assertTrue(email.matches(EMAIL_REGEX_PATTERN));

        email="username@domain.co.in";
        assertTrue(email.matches(EMAIL_REGEX_PATTERN));

        email="user_name@domain.com";
        assertTrue(email.matches(EMAIL_REGEX_PATTERN));
    }

    @Test
    void isValid_whenEmailNotValid_thenReturnFalse() {
        email="username.@domain.com";
        assertFalse(email.matches(EMAIL_REGEX_PATTERN));

        email=".user.name@domain.com";
        assertFalse(email.matches(EMAIL_REGEX_PATTERN));

        email="user-name@domain.com.";
        assertFalse(email.matches(EMAIL_REGEX_PATTERN));

        email="username@.com";
        assertFalse(email.matches(EMAIL_REGEX_PATTERN));
    }
}