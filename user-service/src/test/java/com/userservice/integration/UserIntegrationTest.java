package com.userservice.integration;

import com.userservice.dto.UserRequestDto;
import com.userservice.dto.UserResponseDto;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    private RestTemplate restTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        userRepository.deleteAll(); // Очищаем перед каждым тестом
    }

    @Test
    void createUser_andGetUserById() {
        // Create
        UserRequestDto requestDto = new UserRequestDto("Test User", "test@example.com", 25);
        HttpEntity<UserRequestDto> entity = new HttpEntity<>(requestDto, createHeaders());

        ResponseEntity<UserResponseDto> createResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.POST,
                entity,
                UserResponseDto.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody().getId());

        Long userId = createResponse.getBody().getId();

        // Get by ID
        ResponseEntity<UserResponseDto> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/" + userId,
                UserResponseDto.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(userId, getResponse.getBody().getId());
        assertEquals("Test User", getResponse.getBody().getName());
    }

    @Test
    void getAllUsers_returnsEmptyListInitially() {
        ResponseEntity<List> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.GET,
                null,
                List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    void updateUser_andDeleteUser() {
        // Create
        UserRequestDto createDto = new UserRequestDto("Original", "original@example.com", 30);
        HttpEntity<UserRequestDto> createEntity = new HttpEntity<>(createDto, createHeaders());
        ResponseEntity<UserResponseDto> createResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/users",
                HttpMethod.POST,
                createEntity,
                UserResponseDto.class);

        Long userId = createResponse.getBody().getId();

        // Update
        UserRequestDto updateDto = new UserRequestDto("Updated", "updated@example.com", 31);
        HttpEntity<UserRequestDto> updateEntity = new HttpEntity<>(updateDto, createHeaders());
        ResponseEntity<UserResponseDto> updateResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/" + userId,
                HttpMethod.PUT,
                updateEntity,
                UserResponseDto.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Updated", updateResponse.getBody().getName());

        // Delete
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "http://localhost:" + port + "/api/users/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify deleted
        try {
            restTemplate.getForObject("http://localhost:" + port + "/api/users/" + userId, UserResponseDto.class);
            fail("Expected 404");
        } catch (Exception e) {
            // OK — 404 или HttpClientErrorException
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
