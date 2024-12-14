package com.example.userservice.app.service.completeregistration;

import com.example.userservice.app.kafka.dto.ApprovedRegisterUserDto;
import com.example.userservice.app.kafka.dto.enums.ClientFlow;


public interface CompleteClientRegistrationService {

    /**
     * Метод делегирует завершение или отмену регистрации клиента разным сервисам,
     * в зависимости от ClientFlow клиента
     *
     * @param approvedRegisterUserDto дто с результатом проверки клиента
     */
    void completeClientRegistration (ApprovedRegisterUserDto approvedRegisterUserDto);

    /**
     * Метод получения типа обрабатываемых клиентов(OLD/NEW) сервисом
     *
     * @return {@code ClientFlow}
     */
    ClientFlow getType();
}