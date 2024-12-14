package com.example.userservice.app.kafka.dto.enums;

/**
 * Enum {@code ClientFlow} представляет информацию о том является ли
 * регистрирущийся пользователь клиентом банка.
 * Возможные варианты:
 * NEW (пользователь не клиент банка, пытается зарегистрироваться)
 * OLD (пользователь клиент банка, пытается зарегистрироваться).
 */
public enum ClientFlow {
    NEW, OLD
}