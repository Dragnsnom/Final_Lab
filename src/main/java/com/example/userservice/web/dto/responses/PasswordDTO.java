package com.example.userservice.web.dto.responses;

import com.example.userservice.web.util.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PasswordDTO {

    @NotBlank
    @Password
    private String password;
}
