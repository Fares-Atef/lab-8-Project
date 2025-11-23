package com.example.services;

import com.example.database.JsonDatabaseManager;
import com.example.models.Course;
import com.example.models.CourseStatus;
import com.example.models.Lesson;
import com.example.models.Student;

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
    // الكورسات المتاحة للطالب (Approved فقط + مش مسجل فيها)
    public List<Course> getAvailableCourses(Student student){
        List<Course> available = new ArrayList<>();
        for(Course c : db.getCourses()){

            // لازم يكون الكورس Approved فقط
            if(c.getStatus() != CourseStatus.APPROVED)
                continue;

            // مش مسجل فيه بالفعل
            if(!student.getEnrolledCourseIds().contains(c.getCourseId())) {
                available.add(c);
            }
        }
        return available;
    }


    // كورسات الطالب التي لم يكملها بالكامل بعد
    public List<Course> getPendingCourses(Student student){
        List<Course> pending = new ArrayList<>();
        for(Course c : student.getEnrolledCourses()){
            boolean completedAll = true;
            if(c.getLessons() != null){
                for(Lesson l : c.getLessons()){
                    if(!student.getCompletedLessonIds().contains(l.getLessonId())){
                        completedAll = false;
                        break;
                    }
                }
            }
            if(!completedAll) pending.add(c);
        }
        return pending;
    }

    // نسخة للـ Admin: كل الكورسات التي في حالة Pending
    public List<Course> getPendingCourses(){
        List<Course> pending = new ArrayList<>();
        for(Course c : db.getCourses()){
            if(c.getStatus() == null || c.getStatus().equals("Pending")){
                pending.add(c);
            }
        }
        return pending;
    }

    public Course getCourseById(int id){
        for(Course c : db.getCourses()) if(c.getCourseId()==id) return c;
        return null;
    }

    public void addCourse(Course course){ db.addCourse(course); }

    public void saveCourses(){ db.saveCourses(); }

    public void deleteCourse(int courseId){
        db.getCourses().removeIf(c -> c.getCourseId() == courseId);
        saveCourses();
    }

}
