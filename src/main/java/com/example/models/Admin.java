package com.example.models;

public class Admin extends User {

    public Admin(String username, String email, String passwordHash) {
        super(username, email, passwordHash, "admin");
    }

    public Admin() {
        super("", "", "", "admin");
    }
}
