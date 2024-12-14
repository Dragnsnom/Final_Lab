package com.example.userservice.persistence.repository;

import com.example.userservice.persistence.model.Verification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends CrudRepository<Verification, String> {
}
