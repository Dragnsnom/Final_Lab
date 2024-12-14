package com.example.userservice.app.service.completeregistration.oldclient;

import com.example.userservice.app.kafka.dto.enums.Approval;

import java.util.UUID;

public interface CompleteOldApprovedClientRegistrationService {

    /**
     * Метод завершает или отменяет регистрацию старого клиента,
     * в зависимости от Approval клиента
     *
     * @param clientId id клиента
     */
    void completeOldApprovedClientRegistration(UUID clientId);

    /**
     * Метод получения типа операции сервиса
     * Approval.ALLOWED - успешное завершение регистрации
     * Approval.BLOCKED - отмена регистрации
     *
     * @return {@code Approval}
     */
    Approval getType();
}