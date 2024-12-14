package com.example.userservice.web.dto.responses;

import com.example.userservice.web.util.annotation.Email;
import com.example.userservice.web.util.annotation.Passport;
import com.example.userservice.web.util.annotation.PhoneNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing information about the user.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserInfoDto {

    /**
     * First name of the user
     */
    @NotNull(message = "First name cannot be null")
    private String firstName;

    /**
     * Last name of the user
     */
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    /**
     * Surname of the user
     */
    @NotNull(message = "Surname cannot be null")
    private String surname;

    /**
     * Email of the user
     */
    @Email
    @NotNull(message = "Email cannot be null")
    private String email;

    /**
     * Phone of the user
     */
    @PhoneNumber
    @NotNull(message = "Phone cannot be null")
    private String mobilePhone;

    /**
     * Passport number of the user
     */
    @Passport
    @NotNull(message = "Passport number cannot be null")
    private String passportNumber;
}
