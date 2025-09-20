package com.userservice.dao;

import com.userservice.model.User;
import com.userservice.util.TestHibernateUtil;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoImplIT {

    private static UserDao userDao;

    @BeforeAll
    static void setUpAll() {
        userDao = new UserDaoImpl();
    }

    @AfterAll
    static void tearDownAll() {
        TestHibernateUtil.shutdown();
    }

    private String generateUniqueEmail() {
        return "user_" + UUID.randomUUID() + "@mail.com";
    }

    @Test
    @Order(1)
    void testSaveUser() {
        String email = generateUniqueEmail();
        User user = new User("Test User", email, 25);
        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getName());
        assertEquals(email, saved.getEmail());
        assertEquals(25, saved.getAge());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    @Order(2)
    void testFindById() {
        User user = new User("Fred", generateUniqueEmail(), 30);
        User saved = userDao.save(user);

        Optional<User> found = userDao.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @Order(3)
    void testFindByIdNotFound() {
        Optional<User> found = userDao.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    @Order(4)
    void testFindAll() {
        List<User> beforeTheChange = userDao.findAll();
        int size = beforeTheChange.size();

        userDao.save(new User("Alice", generateUniqueEmail(), 30));
        userDao.save(new User("Bob", generateUniqueEmail(), 50));
        userDao.save(new User("Charlie", generateUniqueEmail(), 28));

        List<User> users = userDao.findAll();
        assertEquals(size + 3, users.size());
    }

    @Test
    @Order(5)
    void testUpdateUser() {
        String email = generateUniqueEmail();
        User user = new User("Original", email, 40);
        User saved = userDao.save(user);
        Long id = saved.getId();

        saved.setName("Updated");
        String updateEmail = generateUniqueEmail();
        saved.setEmail(updateEmail);
        saved.setAge(41);

        User updated = userDao.update(saved);

        assertEquals("Updated", updated.getName());
        assertEquals(updateEmail, updated.getEmail());
        assertEquals(41, updated.getAge());
        assertEquals(id, updated.getId());
    }


    @Test
    @Order(6)
    void testDeleteUser() {
        User user = new User("ToDelete", generateUniqueEmail(), 50);
        User saved = userDao.save(user);
        Long id = saved.getId();

        userDao.deleteById(id);

        Optional<User> deleted = userDao.findById(id);
        assertFalse(deleted.isPresent());
    }


    @Test
    @Order(7)
    void testDeleteNotExistentUser() {
        assertDoesNotThrow(() -> userDao.deleteById(99999L));
    }
}
