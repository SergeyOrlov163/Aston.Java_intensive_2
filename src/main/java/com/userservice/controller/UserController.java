package com.userservice.controller;

import com.userservice.service.UserService;
import com.userservice.dto.UserRequestDto;
import com.userservice.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
            content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UserResponseDto.class)))
    public ResponseEntity<EntityModel<UserResponseDto>> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        EntityModel<UserResponseDto> createdUser = userService.createUser(requestDto);
        return ResponseEntity.created(null).body(createdUser);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден",
            content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<EntityModel<UserResponseDto>> getUserById(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        EntityModel<UserResponseDto> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UserResponseDto.class)))
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDto>>> getAllUsers() {
        CollectionModel<EntityModel<UserResponseDto>> users = CollectionModel.of(userService.getAllUsers());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён",
            content = @Content(mediaType = "application/hal+json",
                    schema = @Schema(implementation = UserResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<EntityModel<UserResponseDto>> updateUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto requestDto) {
        EntityModel<UserResponseDto> updatedUser = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID пользователя", required = true)
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
