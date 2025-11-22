package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {

    private transient List<Course> createdCourses;

    public Instructor(String username, String email, String passwordHash){
        super(username,email,passwordHash,"instructor");
        this.createdCourses = new ArrayList<>();
    }

    public Instructor() {
        super("", "", "", "instructor");
        this.createdCourses = new ArrayList<>();
    }

    public List<Course> getCreatedCourses() {
        return createdCourses;
    }

    public void setCreatedCourses(List<Course> createdCourses) {
        this.createdCourses = createdCourses != null ? createdCourses : new ArrayList<>();
    }
}
