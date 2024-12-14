package com.example.userservice.app.service;

import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.web.dto.requests.AuthorizationTypeIncomingDto;
import com.example.userservice.web.dto.responses.AuthResponseDto;
import com.example.userservice.web.dto.responses.AuthorizationOutgoingDto;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    UserProfile findById(UUID id) throws SQLException;

    List<UserProfile> findAll();

    void deleteById(UUID id);

    UserProfile save(UserProfile userProfile);

    HttpStatus resetPassword(UUID id, String password);

    void updatePassword(UUID id, String newPassword);

    boolean passwordExists(UUID id, String oldPassword);

    void updateSecurityQuestion(UUID id, String securityQuestion, String securityAnswer);

    AuthorizationOutgoingDto changeAuthorizationType (UserProfile profile,
                                                      AuthorizationTypeIncomingDto dto);

    HttpStatus saveFingerprint(UUID id, String fingerprint);

    AuthResponseDto authorizationByFingerprint(String clientId, String fingerprint);

    UserProfile findUserProfileByClientId(UUID clientId);
}
