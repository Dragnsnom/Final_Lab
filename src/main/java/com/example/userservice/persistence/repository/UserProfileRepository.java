package com.example.userservice.persistence.repository;

import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findUserProfileByClient(Client client);

    UserProfile findUserProfileByClientId(UUID clientId);

    Optional<UserProfile> findByClientId(UUID clientId);

    Optional<UserProfile> findUserProfilesByFingerprint(String fingerprint);

}
