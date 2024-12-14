package com.example.userservice.web.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "changing notification status input")
@Data
public class PushNotificationsIncomingDto {

    @NotNull
    @Schema(description = "notification status of user", example = "true",
            type = "boolean", format = "boolean")
    private Boolean notificationStatus;
}
