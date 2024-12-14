package com.example.userservice.web.controller.exception;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ForbiddenException extends RuntimeException {
    private final long remainingSeconds;

    public ForbiddenException(String message, LocalDateTime dateTime) {
        super(message);
        ZonedDateTime zonedBlockExpiration = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
        ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        remainingSeconds = zonedBlockExpiration.toInstant().getEpochSecond() - zonedNow.toInstant().getEpochSecond();
    }

    public String getRemainingSeconds() {
        return remainingSeconds + " seconds";
    }
}
