package com.example.userservice.web.controller.exception;


/**
 * Constructs a new runtime exception with the specified detail message.
 * The cause is not initialized, and may subsequently be initialized by a
 * call to {@link #initCause}.
 *
 */

public class ClientNotFoundException extends RuntimeException{
    private final static String MESSAGE = "Client not found by this phone number - %s";

    public ClientNotFoundException(String mobilePhone) {
        super(String.format(MESSAGE, mobilePhone));
    }
}
