package com.example.services;

import com.example.models.Course;
import com.example.models.Lesson;

import java.util.List;

public class LessonService {
    private static LessonService instance;

    private LessonService(){}

    public static LessonService getInstance(){
        if(instance==null) instance = new LessonService();
        return instance;
    }

    public List<Lesson> getLessonsByCourse(Course course){
        return course.getLessons();
    }
}
