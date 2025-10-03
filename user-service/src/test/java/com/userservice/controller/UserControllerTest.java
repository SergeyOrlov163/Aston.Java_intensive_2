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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_validData_returnsCreatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("John", "john@example.com", 30);
        UserResponseDto expected = new UserResponseDto(1L, "John", "john@example.com", 30, LocalDateTime.now());

        when(userService.createUser(any(UserRequestDto.class))).thenReturn(expected);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(json, UserResponseDto.class);

        assertNotNull(actual.getId());
        assertEquals("John", actual.getName());
        assertEquals("john@example.com", actual.getEmail());
        assertEquals(30, actual.getAge());
        assertNotNull(actual.getCreateAt());
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        UserResponseDto expected = new UserResponseDto(1L, "John", "john@example.com", 30, LocalDateTime.now());

        when(userService.getUserById(eq(1L))).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(json, UserResponseDto.class);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAge(), actual.getAge());
        assertEquals(expected.getCreateAt(), actual.getCreateAt());
    }

    @Test
    void getAllUsers_returnsListOfUsers() throws Exception {
        UserResponseDto user1 = new UserResponseDto(1L, "Alice", "alice@example.com", 25, LocalDateTime.now());
        UserResponseDto user2 = new UserResponseDto(2L, "Bob", "bob@example.com", 30, LocalDateTime.now());
        List<UserResponseDto> expectedList = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(expectedList);

        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserResponseDto[] actualArray = objectMapper.readValue(json, UserResponseDto[].class);
        List<UserResponseDto> actualList = List.of(actualArray);

        assertEquals(2, actualList.size());
        assertEquals(user1.getName(), actualList.get(0).getName());
        assertEquals(user2.getName(), actualList.get(1).getName());
    }

    @Test
    void updateUser_validData_returnsUpdatedUser() throws Exception {
        UserRequestDto requestDto = new UserRequestDto("Updated", "updated@example.com", 31);
        UserResponseDto expected = new UserResponseDto(1L, "Updated", "updated@example.com", 31, LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(expected);

        MvcResult result = mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        UserResponseDto actual = objectMapper.readValue(json, UserResponseDto.class);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAge(), actual.getAge());
    }

    @Test
    void deleteUser_returnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(eq(1L));

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
