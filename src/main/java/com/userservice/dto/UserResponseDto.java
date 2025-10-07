package com.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto extends RepresentationModel<UserResponseDto> {

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
