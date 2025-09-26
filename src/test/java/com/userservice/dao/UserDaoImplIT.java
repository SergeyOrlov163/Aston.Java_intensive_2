package com.userservice.dao;

import com.userservice.model.User;
import com.userservice.util.TestHibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

    @BeforeEach
    void cleanDatabase() {
        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            tx.commit();
        }
    }

    private String generateUniqueEmail() {
        return "user_" + UUID.randomUUID() + "@mail.com";
    }

    @Test
    void testSaveUser() {
        String email = generateUniqueEmail();
        User user = new User("Test User", email, 25);

        User saved = userDao.save(user);

        assertNotNull(saved.getId());
        assertEquals("Test User", saved.getName());
        assertEquals(email, saved.getEmail());
        assertEquals(25, saved.getAge());
        assertNotNull(saved.getCreatedAt());

        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            User fromDb = session.get(User.class, saved.getId());
            assertNotNull(fromDb);
            assertEquals(saved, fromDb);
        }
    }

    @Test
    void testFindById() {
        Long userId;
        String email = generateUniqueEmail();
        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = new User("Fred", email, 30);
            session.persist(user);
            tx.commit();
            userId = user.getId();
        }

        Optional<User> found = userDao.findById(userId);

        assertTrue(found.isPresent());
        assertEquals(userId, found.get().getId());
        assertEquals("Fred", found.get().getName());
        assertEquals(email, found.get().getEmail());
        assertEquals(30, found.get().getAge());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<User> found = userDao.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll() {
        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User alice = new User("Alice", "alice_" + UUID.randomUUID() + "@mail.com", 30);
            User bob = new User("Bob", "bob_" + UUID.randomUUID() + "@mail.com", 50);
            User charlie = new User("Charlie", "charlie_" + UUID.randomUUID() + "@mail.com", 28);

            session.persist(alice);
            session.persist(bob);
            session.persist(charlie);

            tx.commit();
        }

        List<User> users = userDao.findAll();

        assertEquals(3, users.size(), "Expected exactly 3 users in database");

        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Alice")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Bob")));
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("Charlie")));
    }

    @Test
    void testUpdateUser() {
        Long userId;
        String email = generateUniqueEmail();
        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = new User("Original", email, 40);
            session.persist(user);
            tx.commit();
            userId = user.getId();
        }

        String updateEmail = generateUniqueEmail();

        User updated = userDao.update(new User(userId, "Updated", updateEmail, 41, null));

        assertEquals("Updated", updated.getName());
        assertEquals(updateEmail, updated.getEmail());
        assertEquals(41, updated.getAge());
        assertEquals(userId, updated.getId());

        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            User fromDb = session.get(User.class, userId);
            assertNotNull(fromDb);
            assertEquals("Updated", fromDb.getName());
            assertEquals(updateEmail, fromDb.getEmail());
            assertEquals(41, fromDb.getAge());
        }
    }


    @Test
    void testDeleteUser() {
        Long userId;
        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = new User("ToDelete", generateUniqueEmail(), 50);
            session.persist(user);
            tx.commit();
            userId = user.getId();
        }

        userDao.deleteById(userId);

        try (Session session = TestHibernateUtil.getSessionFactory().openSession()) {
            User fromDb = session.get(User.class, userId);
            assertNull(fromDb, "User should be deleted");
        }
    }


    @Test
    void testDeleteNotExistentUser() {
        assertDoesNotThrow(() -> userDao.deleteById(99999L));
    }
}
