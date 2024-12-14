package com.example.userservice.web.dto.errors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseDTO {

    private String uri;

    private String type;

    private String message;

    private String timestamp;

    public ErrorResponseDTO(String type, String message) {
        this.uri = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        this.timestamp = LocalDateTime.now().toString();
        this.type = type;
        this.message = message;
    }
}
