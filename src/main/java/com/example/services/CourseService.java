package com.example.services;

import com.example.models.Course;
import com.example.models.Student;
import com.example.database.JsonDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private static CourseService instance;
    private JsonDatabaseManager db;

    private CourseService() { db = JsonDatabaseManager.getInstance(); }

    public static CourseService getInstance() {
        if(instance == null) instance = new CourseService();
        return instance;
    }

    // الكورسات المتاحة للطالب (غير مسجل فيها)
    public List<Course> getAvailableCourses(Student student){
        List<Course> available = new ArrayList<>();
        for(Course c : db.getCourses()){
            if(!student.getEnrolledCourseIds().contains(c.getCourseId())) available.add(c);
        }
        return available;
    }

    public Course getCourseById(int id){
        for(Course c : db.getCourses()) if(c.getCourseId()==id) return c;
        return null;
    }

    public void addCourse(Course course){ db.addCourse(course); }

    public void saveCourses(){ db.saveCourses(); }
}
