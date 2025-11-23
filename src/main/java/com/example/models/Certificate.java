package com.example.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Certificate {
    private String certificateId;
    private int studentId;
    private int courseId;
    private String issueDate;

    public Certificate() {}

    // ميثود إنشاء شهادة جديدة تلقائياً
    public Certificate(int courseId, int studentId) {
        this.certificateId = UUID.randomUUID().toString();
        this.courseId = courseId;
        this.studentId = studentId;
        this.issueDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // Getters
    public String getCertificateId() { return certificateId; }
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public String getIssueDate() { return issueDate; }
}
