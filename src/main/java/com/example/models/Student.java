package com.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.database.JsonDatabaseManager;
import com.example.services.CourseService;

public class Student extends User {
    private List<Integer> enrolledCourseIds;
    private List<Integer> completedLessonIds;
    private Map<Integer, Integer> quizScores = new HashMap<>();
    private List<Certificate> certificates = new ArrayList<>();

    public Student(String username, String email, String passwordHash){
        super(username,email,passwordHash,"student");
        this.enrolledCourseIds = new ArrayList<>();
        this.completedLessonIds = new ArrayList<>();
    }

    public Student() {
        super("", "", "", "student");
        this.enrolledCourseIds = new ArrayList<>();
        this.completedLessonIds = new ArrayList<>();
    }

    public List<Course> getEnrolledCourses() {
        List<Course> courses = new ArrayList<>();
        for(Integer id : enrolledCourseIds){
            Course c = CourseService.getInstance().getCourseById(id);
            if(c != null) courses.add(c);
        }
        return courses;
    }

    public void enrollCourse(Course course){
        if(course != null && !enrolledCourseIds.contains(course.getCourseId())){
            enrolledCourseIds.add(course.getCourseId());
            course.enrollStudent(this);
            JsonDatabaseManager.getInstance().saveUsers();
            CourseService.getInstance().saveCourses();
        }
    }

    public boolean markLessonCompleted(Course course, Lesson lesson){
        if(lesson != null && !completedLessonIds.contains(lesson.getLessonId())){
            completedLessonIds.add(lesson.getLessonId());
            JsonDatabaseManager.getInstance().saveUsers();
            return true;
        }
        return false;
    }

    public int getProgress(Course course){
        List<Lesson> lessons = course.getLessons();
        if (lessons == null || lessons.isEmpty()) return 0;
        long completed = lessons.stream()
                .filter(l -> completedLessonIds.contains(l.getLessonId()))
                .count();
        return (int)((completed * 100)/lessons.size());
    }

    public List<Integer> getEnrolledCourseIds() { return enrolledCourseIds; }
    public void setEnrolledCourseIds(List<Integer> enrolledCourseIds) { this.enrolledCourseIds = enrolledCourseIds != null ? enrolledCourseIds : new ArrayList<>(); }
    public List<Integer> getCompletedLessonIds() { return completedLessonIds; }
    public void setCompletedLessonIds(List<Integer> completedLessonIds) { this.completedLessonIds = completedLessonIds != null ? completedLessonIds : new ArrayList<>(); }
    public void saveQuizScore(int lessonId, int score) {
        quizScores.put(lessonId, score);
    }

    public Integer getQuizScore(int lessonId) {
        return quizScores.getOrDefault(lessonId, null);
    }

    public Map<Integer, Integer> getQuizScores() { return quizScores; }
    public void setQuizScores(Map<Integer, Integer> quizScores) {
        this.quizScores = quizScores != null ? quizScores : new HashMap<>();
    }
    public List<Certificate> getCertificates() { return certificates; }
    public void addCertificate(Certificate c) { certificates.add(c); }
}
