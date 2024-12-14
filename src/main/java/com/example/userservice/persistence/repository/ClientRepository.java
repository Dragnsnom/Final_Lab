package com.example.userservice.persistence.repository;

import com.example.userservice.app.enums.ClientStatus;
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
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findClientByContact(Contact contact);

    /**
     * Method for changing client status by ID.
     *
     * @param id user identifier
     * @param status new client status
     * @return the number of records updated (should be 1 if the update was successful)
     */
    @Modifying
    @Query("UPDATE Client c SET c.clientStatus = :status WHERE c.id = :id")
    void changeClientStatusById(@Param("id") UUID id, @Param("status") ClientStatus status);
}