package com.example.userservice.app.kafka.dto.enums;

/**
 * Enum {@code Approval} представляет различные типы подтвержления регистрации пользователя.
 * Возможные варианты:
 * ALLOWED (пользователь прошел проверку)
 * BLOCKED (пользователь не прошел проверку).
 */
public enum Approval {

    ALLOWED, BLOCKED
}
