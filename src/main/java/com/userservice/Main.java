package com.userservice;

import com.userservice.util.HibernateUtil;
import com.userservice.dao.UserDao;
import com.userservice.dao.UserDaoImpl;
import com.userservice.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Starting User Service Console Application...");

        while (true) {
            showMenu();
            int choice = getIntInput("Enter your choice");

            try {
                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> readUser();
                    case 3 -> updateUser();
                    case 4 -> deleteUser();
                    case 5 -> listAllUsers();
                    case 0 -> {
                        logger.info("Shutting down application...");
                        HibernateUtil.shutdown();
                        System.out.println("Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                logger.error("An error occurred during operation", e);
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n=== User Service ===");
        System.out.println("1. Create User");
        System.out.println("2. Read User by ID");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. List All Users");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void createUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        int age = getIntInput("Enter age");

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("User created: " + user);
    }

    private static void readUser() {
        Long id = getLongInput("Enter user ID");
        var userOpt = userDao.findById(id);
        if (userOpt.isPresent()) {
            System.out.println("Found: " + userOpt.get());
        } else {
            System.out.println("User not found with ID: " + id);
        }
    }

    private static void updateUser() {
        Long id = getLongInput("Enter user ID to update");

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();

        System.out.print("Enter new email: ");
        String email = scanner.nextLine();

        int age = getIntInput("Enter new age");

        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        try {
            userDao.update(user);
            System.out.println("User updated: " + user);
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        Long id = getLongInput("Enter user ID to delete");
        userDao.deleteById(id);
        System.out.println("User deleted (if existed).");
    }

    private static void listAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            users.forEach(System.out::println);
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static long getLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + ": ");
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}