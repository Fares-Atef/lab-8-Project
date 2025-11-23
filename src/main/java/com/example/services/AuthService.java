package com.example.services;

import com.example.database.JsonDatabaseManager;
import com.example.models.Admin;
import com.example.models.Instructor;
import com.example.models.Student;
import com.example.models.User;

import java.security.MessageDigest;
import java.util.List;

public class AuthService {
    private static AuthService instance;
    private JsonDatabaseManager db;

    private AuthService() {
        db = JsonDatabaseManager.getInstance();
        createDefaultAdmin(); // إنشاء Admin لو مش موجود
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    // ================= Create Default Admin =================
    private void createDefaultAdmin() {
        boolean adminExists = db.getUsers().stream()
                .anyMatch(u -> u.getRole().equalsIgnoreCase("admin"));

        if (!adminExists) {
            String username = "admin";
            String email = "admin@system.com";
            String password = "admin123"; // ممكن تغيره

            String hashed = hashPassword(password);
            User admin = new Admin(username, email, hashed);

            db.addUser(admin);

            System.out.println("======================================");
            System.out.println(" Default Admin Created ");
            System.out.println(" Email: admin@system.com ");
            System.out.println(" Password: admin123 ");
            System.out.println("======================================");
        }
    }

    // ================= Signup =================
    public boolean signup(String username, String email, String password, String role) {
        List<User> users = db.getUsers();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Signup failed: Email already exists!");
                return false;
            }
        }

        String hashed = hashPassword(password);
        User user;

        role = role.toLowerCase();

        switch (role) {
            case "admin":
                user = new Admin(username, email, hashed);
                break;

            case "instructor":
                user = new Instructor(username, email, hashed);
                break;

            default:
                user = new Student(username, email, hashed);
        }

        db.addUser(user);
        System.out.println("User added successfully: " + username + " (" + role + ")");
        return true;
    }

    // ================= Login =================
    public User login(String email, String password) {
        String hashed = hashPassword(password);

        for (User u : db.getUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)
                    && u.getPasswordHash().equals(hashed)) {

                System.out.println("Login successful: " + email + " - Role: " + u.getRole());
                return u;
            }
        }

        System.out.println("Login failed: Invalid credentials for " + email);
        return null;
    }

    // ================= SHA-256 Hashing =================
    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
