package com.example.userservice.web.controller.exception;

public class InternalServerException extends RuntimeException{
    public InternalServerException(String message) {
        super(message);
    }
}
