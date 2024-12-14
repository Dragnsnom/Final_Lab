package com.example.userservice.web.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FingerprintDTO {
    @NotBlank
    private String fingerprint;
}

