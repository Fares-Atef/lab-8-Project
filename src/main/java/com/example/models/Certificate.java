package com.example.models;

public class Certificate {
    private String certificateId;
    private int studentId;
    private int courseId;
    private String issueDate;

    public Certificate() {}

    public Certificate(String certificateId, int studentId, int courseId, String issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.issueDate = issueDate;
    }

    public String getCertificateId() { return certificateId; }
    public int getStudentId() { return studentId; }
    public int getCourseId() { return courseId; }
    public String getIssueDate() { return issueDate; }
}
