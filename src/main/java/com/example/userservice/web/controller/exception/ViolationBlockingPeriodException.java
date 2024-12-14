package com.example.userservice.web.controller.exception;

public class ViolationBlockingPeriodException extends RuntimeException{
    public ViolationBlockingPeriodException(String message) {
        super(message);
    }
}
