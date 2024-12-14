package com.example.userservice.web.mapper;

import com.example.userservice.persistence.model.Client;
import com.example.userservice.persistence.model.Contact;
import com.example.userservice.persistence.model.UserProfile;
import com.example.userservice.web.dto.requests.ClientDto;
import com.example.userservice.web.dto.requests.NonClientDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClientMapper {

    @Mapping(target = "contact.mobilePhone", source = "mobilePhone")
    @Mapping(target = "contact.email", source = "email")
    @Mapping(target = "passportData.identificationPassportNumber", source = "passportNumber")
    @Mapping(target = "passportData.issuanceDate", source = "issuanceDate")
    @Mapping(target = "passportData.placeOfIssue", source = "placeOfIssue")
    @Mapping(target = "passportData.expiryDate", source = "expiryDate")
    @Mapping(target = "passportData.birthDate", source = "birthDate")
    @Mapping(target = "userProfile.passwordEncoded",
            expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()" +
                    ".encode(nonClientDto.getPassword()))")
    @Mapping(target = "userProfile.securityQuestion", source = "securityQuestion")
    @Mapping(target = "userProfile.securityAnswer", source = "securityAnswer")
    Client toClient (NonClientDto nonClientDto);

    @Mapping(target = "id", ignore = true)
    Contact toContact (ClientDto clientDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordEncoded", source = "password")
    UserProfile toUserProfile (ClientDto clientDto);
}
