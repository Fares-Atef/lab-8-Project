package com.example.services;

import com.example.models.User;
import com.example.models.Student;
import com.example.models.Instructor;
import com.example.database.JsonDatabaseManager;

import java.security.MessageDigest;
import java.util.List;

public class AuthService {
    private static AuthService instance;
    private JsonDatabaseManager db;

    private AuthService() {
        db = JsonDatabaseManager.getInstance();
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    // ================= Signup =================
    public boolean signup(String username, String email, String password, String role) {
        List<User> users = db.getUsers();
        for(User u : users) {
            if(u.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Signup failed: Email already exists!");
                return false; // duplicate email
            }
        }

        String hashed = hashPassword(password);
        User user;

        // force role to lowercase
        if(role.equalsIgnoreCase("instructor")){
            user = new Instructor(username, email, hashed);
        } else {
            user = new Student(username, email, hashed);
        }

        db.addUser(user); // سيتم حفظ المستخدم مباشرة في JSON
        System.out.println("User added successfully: " + username + " (" + role.toLowerCase() + ")");
        return true;
    }

    // ================= Login =================
    public User login(String email, String password){
        String hashed = hashPassword(password);
        for(User u : db.getUsers()){
            if(u.getEmail().equalsIgnoreCase(email) && u.getPasswordHash().equals(hashed)) {
                System.out.println("Login successful: " + email);
                return u;
            }
        }
        System.out.println("Login failed: Invalid credentials for " + email);
        return null;
    }

    // ================= SHA-256 Hashing =================
    private String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(byte b : hash){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
