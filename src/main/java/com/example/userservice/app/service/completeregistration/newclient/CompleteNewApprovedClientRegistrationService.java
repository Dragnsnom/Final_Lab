package com.example.userservice.app.service.completeregistration.newclient;

import com.example.userservice.app.kafka.dto.enums.Approval;

import java.util.UUID;

public interface CompleteNewApprovedClientRegistrationService {

    /**
     * Метод завершает или отменяет регистрацию нового клиента,
     * в зависимости от Approval клиента
     *
     * @param clientId id клиента
     */
    void completeNewApprovedClientRegistration(UUID clientId);

    /**
     * Метод получения типа операции сервиса
     * Approval.ALLOWED - успешное завершение регистрации
     * Approval.BLOCKED - отмена регистрации
     *
     * @return {@code Approval}
     */
    Approval getType();
}