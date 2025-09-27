package com.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.UserRequestDto;
import com.userservice.dto.UserResponseDto;
import com.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_validData_returnsCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("John", "john@example.com", 30);
        UserResponseDto responseDto = new UserResponseDto(
                1L, "John", "john@example.com", 30, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        UserResponseDto responseDto = new UserResponseDto(
                1L, "John", "john@example.com", 30, LocalDateTime.now());

        when(userService.getUserById(eq(1L))).thenReturn(responseDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Alice", "alice@example.com", 25, LocalDateTime.now());
        UserResponseDto user2 = new UserResponseDto(2L, "Bob", "bob@example.com", 30, LocalDateTime.now());

        when(userService.getAllUsers()).thenReturn(java.util.List.of(user1, user2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void updateUser_validData_returnsUpdatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated", "updated@example.com", 31);
        UserResponseDto responseDto = new UserResponseDto(
                1L, "Updated", "updated@example.com", 31, LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(eq(1L));

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
