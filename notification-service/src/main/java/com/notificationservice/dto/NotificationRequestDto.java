package com.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequestDto {

    @Schema(description = "Email получателя", example = "user@example.com", required = true)
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Операция (CREATE/DELETE)", example = "CREATE", required = true)
    @NotBlank
    private String operation;
}
