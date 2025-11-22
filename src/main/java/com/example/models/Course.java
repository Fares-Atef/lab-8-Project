package com.example.models;

import java.util.ArrayList;
import java.util.List;
import com.example.database.JsonDatabaseManager;

public class Course {
    private static int courseCounter = 1;

    private int courseId;
    private String title;
    private String description;
    private Instructor instructor;
    private String instructorEmail;
    private List<Lesson> lessons;
    private List<Student> enrolledStudents;
    private CourseStatus status = CourseStatus.PENDING;
    private Integer approvedByAdminId = null;
    private String rejectionReason = null;

    // ======== مهم جداً لـ GSON ========
    public Course() {
        this.courseId = courseCounter++;
        this.lessons = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }

    public Course(String title, String description, Instructor instructor){
        this.courseId = courseCounter++;
        this.title = (title != null) ? title : "Untitled Course";
        this.description = (description != null) ? description : "";
        setInstructor(instructor);
        this.lessons = new ArrayList<>();
        this.enrolledStudents = new ArrayList<>();
    }

    public void setCourseId(int id){
        this.courseId = id;
        if(id >= courseCounter)
            courseCounter = id + 1;   // ★ يمنع تكرار الـ IDs
    }

    public void setInstructorEmail(String email){
        this.instructorEmail = email;
    }

    public void setTitle(String title){
        this.title = (title != null) ? title : this.title;
    }

    public void setDescription(String description){
        this.description = (description != null) ? description : this.description;
    }

    public void setLessons(List<Lesson> lessons){
        this.lessons = (lessons != null) ? lessons : new ArrayList<>();
    }

    public void setEnrolledStudents(List<Student> students){
        this.enrolledStudents = (students != null) ? students : new ArrayList<>();
    }

    // ======== Instructor Handling ========
    public void setInstructor(Instructor instructor){
        this.instructor = instructor;
        this.instructorEmail = (instructor != null) ? instructor.getEmail() : null;
    }

    public Instructor getInstructor() {
        if(this.instructor == null && instructorEmail != null){
            this.instructor = (Instructor) JsonDatabaseManager.getInstance()
                    .getUsers().stream()
                    .filter(u -> u instanceof Instructor && u.getEmail().equals(instructorEmail))
                    .findFirst()
                    .orElse(null);
        }
        return this.instructor;
    }

    public void addLesson(Lesson lesson){
        if(lesson != null){
            if(lessons == null) lessons = new ArrayList<>();
            lessons.add(lesson);
        }
    }

    public void removeLesson(Lesson lesson){
        if(lesson != null && lessons != null){
            lessons.remove(lesson);
        }
    }

    public List<Lesson> getLessons(){
        if(lessons == null) lessons = new ArrayList<>();
        return lessons;
    }

    public List<Student> getEnrolledStudents(){
        if(enrolledStudents == null) enrolledStudents = new ArrayList<>();
        return enrolledStudents;
    }

    public void enrollStudent(Student student){
        if(student != null){
            if(enrolledStudents == null) enrolledStudents = new ArrayList<>();
            if(!enrolledStudents.contains(student)){
                enrolledStudents.add(student);
            }
        }
    }
    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) { this.status = status; }

    public Integer getApprovedByAdminId() { return approvedByAdminId; }
    public void setApprovedByAdminId(Integer id) { this.approvedByAdminId = id; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }

    public int getCourseId(){ return courseId; }
    public String getTitle(){ return title; }
    public String getDescription(){ return description; }
    public String getInstructorEmail(){ return instructorEmail; }

    public static void setCourseCounter(int value){ courseCounter = value; }
}
