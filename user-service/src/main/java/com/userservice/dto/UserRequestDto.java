package com.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @Schema(description = "Имя пользователя", example = "John Doe", required = true)
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Email пользователя", example = "john@example.com", required = true)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30", required = true)
    @NotNull(message = "Age is required")
    @Positive(message = "Age must be positive")
    private Integer age;
}
