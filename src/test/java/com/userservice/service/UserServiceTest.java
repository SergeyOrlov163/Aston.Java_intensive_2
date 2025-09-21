package com.userservice.service;

import com.userservice.dao.UserDao;
import com.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "Test User", "test@mail.com", 25, null);
    }

    @Test
    void createUser_validate_returnsSavedUser() {
        when(userDao.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("Test User", "test@mail.com", 25);

        assertNotNull(result);
        assertEquals("Test User", result.getName());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertNotNull(capturedUser);
        assertEquals("Test User", capturedUser.getName());
        assertEquals("test@mail.com", capturedUser.getEmail());
        assertEquals(25, capturedUser.getAge());
        assertNull(capturedUser.getId());
        assertNotNull(capturedUser.getCreatedAt());

        assertEquals(testUser, result);
    }

    @Test
    void createUser_nullName_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(null, "test@mail.com", 25)
        );
        assertEquals("Name cannot be empty", exception.getMessage());
        verify(userDao, never()).save(any());
    }

    @Test
    void createUser_invalidEmail_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Test", "invalid-email", 25)
        );
        assertEquals("invalid email", exception.getMessage());
        verify(userDao, never()).save(any());
    }

    @Test
    void createUser_negativeAge_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Test", "test@example.com", -5)
        );
        assertEquals("Age most be non-negative", exception.getMessage());
        verify(userDao, never()).save(any());
    }

    @Test
    void getUserById_returnsUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userDao,times(1)).findById(1L);
    }

    @Test
    void getAllUsers_returnListOfUsers() {
        List<User> mockUsers = Arrays.asList(testUser,
                new User(2L, "Alice", "alice@mail.com", 30, null));
        when(userDao.findAll()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void updateUser_callsDaoUpdate() {
        when(userDao.update(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, "Updated", "updated@mail.com", 26);

        assertNotNull(result);
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    void deleteUser_callsDaoDelete() {
        userService.deleteUser(1L);

        verify(userDao, times(1)).deleteById(1L);
    }
}
