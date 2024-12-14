package com.example.userservice.web.controller.exception;

public class UnauthorizedException extends RuntimeException{
    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause user not authorized
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public UnauthorizedException(String message) {
        super(message);
    }
}