package com.example.userservice.web.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Changing notification input dto")
public class NotificationStatusDto {
    @NotNull
    @Schema(description = "notification status of user", example = "true",
            type = "boolean")
    Boolean notificationStatus;
}
