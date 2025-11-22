package com.example.models;

import java.io.Serializable;

public abstract class User implements Serializable {
    protected int userId;
    protected String username;
    protected String email;
    protected String passwordHash;
    protected String role;

    private static int userCounter = 1;

    public User(String username, String email, String passwordHash, String role){
        this.userId = userCounter++;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }


    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
