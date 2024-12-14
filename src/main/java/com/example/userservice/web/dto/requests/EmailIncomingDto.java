package com.example.userservice.web.dto.requests;

import com.example.userservice.web.util.annotation.Email;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
@Schema(description = "changing email input")
public class EmailIncomingDto {
    @NotBlank(message = "User email cannot be empty")
    @Email
    @Size(max = 50)
    @Schema(description = "email of user; email must be not empty and in email format", example = "batman@mail.ru",
            type = "string", format = "string")
    private String email;
}
