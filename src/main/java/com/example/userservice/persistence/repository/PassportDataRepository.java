package com.example.userservice.persistence.repository;

import com.example.userservice.persistence.model.PassportData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassportDataRepository extends JpaRepository<PassportData, UUID> {

    Optional<PassportData> findByIdentificationPassportNumber(String identificationPassportNumber);

    Optional<PassportData> findPassportDataByIdentificationPassportNumber(String passportNumber);
}
