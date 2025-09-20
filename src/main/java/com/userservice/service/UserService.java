package com.userservice.service;

import com.userservice.dao.UserDao;
import com.userservice.model.User;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name, String email, Integer age) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("invalid email");
        }
        if (age == null || age < 0) {
        throw new IllegalArgumentException ("Age most be non-negative");
        }

        User user = new User(name, email, age);
        return userDao.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateUser(Long id, String name, String email, Integer age) {
        User user =new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }
}
