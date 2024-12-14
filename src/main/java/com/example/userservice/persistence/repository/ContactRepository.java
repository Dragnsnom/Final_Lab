package com.example.userservice.persistence.repository;

import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Optional<Contact> findContactByMobilePhone(String mobilePhone);

    Optional<Contact> findByClient_PassportData_IdentificationPassportNumber(String passportNumber);

    Optional<Contact> findContactByEmail(String email);

    Optional<Contact> findContactByClient_Id(UUID clientId);

    Optional<Contact> findByClient(Client client);

}
