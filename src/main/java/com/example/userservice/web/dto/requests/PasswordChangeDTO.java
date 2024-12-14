package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a password change request.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Schema(description = "Password change input DTO")
public class PasswordChangeDTO {

    /**
     * The old password for the password change request.
     */
    @NotNull(message = "Old password cannot be null")
    @Password(message = "The old password must be no shorter than 6 characters " +
            "and no longer than 20 characters and contain only allowed characters")
    @Schema(description = "Old user password", example = "Pa$$w0rd", type = "string", format = "string")
    private String oldPassword;

    /**
     * The new password for the password change request.
     */
    @NotNull(message = "New password cannot be null")
    @Password(message = "The new password must be no shorter than 6 characters " +
            "and no longer than 20 characters and contain only allowed characters")
    @Schema(description = "New user password", example = "Pa$$w0rd123", type = "string", format = "string")
    private String newPassword;
}
