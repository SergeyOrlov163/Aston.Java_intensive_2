package com.userservice.dto;

import com.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private Integer age;
    private LocalDateTime createAt;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.age = user.getAge();
        this.createAt = user.getCreatedAt();
    }
}
