package com.userservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservice.dto.UserRequestDto;
import com.userservice.dto.UserResponseDto;
import com.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureWebMvc
class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_andGetUserById() throws Exception {
        // Create
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com", 25);

        MvcResult createResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String createJson = createResult.getResponse().getContentAsString();
        UserResponseDto createdUser = objectMapper.readValue(createJson, UserResponseDto.class);

        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("test@example.com", createdUser.getEmail());
        assertEquals(25, createdUser.getAge());
        assertNotNull(createdUser.getCreateAt());

        Long userId = createdUser.getId();

        // Get by ID
        MvcResult getResult = mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();

        String getJson = getResult.getResponse().getContentAsString();
        UserResponseDto retrievedUser = objectMapper.readValue(getJson, UserResponseDto.class);

        assertEquals(userId, retrievedUser.getId());
        assertEquals("Test User", retrievedUser.getName());
        assertEquals("test@example.com", retrievedUser.getEmail());
        assertEquals(25, retrievedUser.getAge());
        assertEquals(createdUser.getCreateAt(), retrievedUser.getCreateAt());

    }

    @Test
    void getAllUsers_returnsEmptyListInitially() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<UserResponseDto> users = List.of(objectMapper.readValue(json, UserResponseDto[].class));

        assertTrue(users.isEmpty());
    }

    @Test
    void updateUser_andDeleteUser() throws Exception {
        // Create
        UserRequestDto createDto = new UserRequestDto("Original", "original@example.com", 30);
        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String createJson = createResult.getResponse().getContentAsString();
        UserResponseDto createdUser = objectMapper.readValue(createJson, UserResponseDto.class);
        Long userId = createdUser.getId();

        // Update
        UserRequestDto updateDto = new UserRequestDto("Updated", "updated@example.com", 31);
        MvcResult updateResult = mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn();

        String updateJson = updateResult.getResponse().getContentAsString();
        UserResponseDto updatedUser = objectMapper.readValue(updateJson, UserResponseDto.class);

        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(31, updatedUser.getAge());

        // Delete
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
