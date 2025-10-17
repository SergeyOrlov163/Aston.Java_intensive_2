package com.userservice.dto;

import com.userservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "John Doe")
    private String name;

    @Schema(description = "Email пользователя", example = "john@example.com")
    private String email;

    @Schema(description = "Возраст пользователя", example = "30")
    private Integer age;

    @Schema(description = "Дата создания", example = "2025-01-01T10:00:00")
    private LocalDateTime createAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.createAt = user.getCreatedAt();
    }
}
