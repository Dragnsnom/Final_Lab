package com.example.userservice.web.dto.responses;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailAndPassportResponseDto {

    @NotNull
    private Boolean status;
}
