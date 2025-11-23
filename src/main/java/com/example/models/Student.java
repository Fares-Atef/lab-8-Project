package com.example.models;

import com.example.database.JsonDatabaseManager;
import com.example.services.CourseService;
import com.example.utils.CertificateGenerator;

import javax.swing.*;
import java.util.*;

public class Student extends User {
    private List<Integer> enrolledCourseIds;
    private List<Integer> completedLessonIds;
    private Map<Integer,Integer> quizScores; // lessonId -> score
    private Map<Integer,Boolean> passedQuizzes; // quizId -> passed
    private List<Certificate> certificates;

    public Student(String username,String email,String passwordHash){
        super(username,email,passwordHash,"student");
        enrolledCourseIds = new ArrayList<>();
        completedLessonIds = new ArrayList<>();
        quizScores = new HashMap<>();
        passedQuizzes = new HashMap<>();
        certificates = new ArrayList<>();
    }
    public Student(){ this("","",""); }

    // ===== Courses =====
    public List<Course> getEnrolledCourses(){
        List<Course> list = new ArrayList<>();
        for(int id: enrolledCourseIds){
            Course c = CourseService.getInstance().getCourseById(id);
            if(c!=null) list.add(c);
        }
        return list;
    }

    public void enrollCourse(Course c){
        if(c!=null && !enrolledCourseIds.contains(c.getCourseId())){
            enrolledCourseIds.add(c.getCourseId());
            c.enrollStudent(this);
            JsonDatabaseManager.getInstance().saveUsers();
            CourseService.getInstance().saveCourses();
        }
    }

    // ===== Lessons =====
    public boolean markLessonCompleted(Course course, Lesson lesson){
        if(lesson!=null && !completedLessonIds.contains(lesson.getLessonId())){
            completedLessonIds.add(lesson.getLessonId());
            JsonDatabaseManager.getInstance().saveUsers();

            // تحقق من إكمال الكورس بالكامل
            if(course != null) checkCourseCompletion(course);

            return true;
        }
        return false;
    }

    public int getProgress(Course course){
        List<Lesson> lessons = course.getLessons();
        if(lessons==null || lessons.isEmpty()) return 0;
        long completed = lessons.stream().filter(l -> completedLessonIds.contains(l.getLessonId())).count();
        return (int)((completed*100)/lessons.size());
    }

    public boolean hasCompletedLesson(Lesson lesson){
        return lesson!=null && completedLessonIds.contains(lesson.getLessonId());
    }

    // ===== Quiz =====
    public void saveQuizScore(int lessonId,int score){ quizScores.put(lessonId,score); }
    public Integer getQuizScore(int lessonId){ return quizScores.getOrDefault(lessonId,null); }
    public boolean hasPassedQuiz(Quiz quiz){ return passedQuizzes.getOrDefault(quiz.getQuizId(),false); }

    public void markQuizPassed(Quiz quiz, Course course){
        if(quiz == null) return;
        passedQuizzes.put(quiz.getQuizId(), true);
        // تحقق إذا الطالب أكمل كل الدروس والكويزات في الكورس
        if(course != null) checkCourseCompletion(course);
    }

    // ===== Course Completion Check =====
    private void checkCourseCompletion(Course course){
        boolean allDone = true;
        for(Lesson l: course.getLessons()){
            if(!hasCompletedLesson(l) || (l.getQuiz()!=null && !hasPassedQuiz(l.getQuiz()))){
                allDone = false;
                break;
            }
        }

        if(allDone && !hasCertificate(course)){
            Certificate cert = CertificateGenerator.generatePDF(this, course);
            addCertificate(cert);
            JOptionPane.showMessageDialog(null,"Congratulations! You completed: "+course.getTitle());
        }
    }

    // ===== Certificates =====
    public boolean hasCertificate(Course course){
        return certificates.stream().anyMatch(c -> c.getCourseId()==course.getCourseId());
    }

    public List<Certificate> getCertificates(){ return certificates; }
    public void addCertificate(Certificate c){ certificates.add(c); }

    // ===== Getters/Setters =====
    public List<Integer> getEnrolledCourseIds() { return enrolledCourseIds; }
    public void setEnrolledCourseIds(List<Integer> e) { enrolledCourseIds = e!=null?e:new ArrayList<>(); }
    public List<Integer> getCompletedLessonIds() { return completedLessonIds; }
    public void setCompletedLessonIds(List<Integer> l){ completedLessonIds = l!=null?l:new ArrayList<>(); }
    public Map<Integer,Integer> getQuizScores(){ return quizScores; }
    public void setQuizScores(Map<Integer,Integer> q){ quizScores = q!=null?q:new HashMap<>(); }
    public Map<Integer, Boolean> getPassedQuizzes() { return passedQuizzes; }
    public void setPassedQuizzes(Map<Integer, Boolean> p) { passedQuizzes = p!=null?p:new HashMap<>(); }
}
