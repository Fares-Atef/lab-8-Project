package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private static int quizCounter = 1;
    private String title;
    private int quizId;
    private List<Question> questions;
    private List<StudentAnswer> studentAnswers; // تخزين إجابات الطلاب
    private int passingScore = 50; // حد النجاح افتراضي 50%

    public Quiz() {
        this.quizId = quizCounter++;
        this.questions = new ArrayList<>();
        this.studentAnswers = new ArrayList<>();
    }
    public Quiz(String title, List<Question> questions) {
        this.quizId = quizCounter++;
        this.title = (title != null && !title.isEmpty()) ? title : "Untitled Quiz";
        this.questions = questions != null ? questions : new ArrayList<>();
        this.studentAnswers = new ArrayList<>();
    }

    public Quiz(List<Question> questions) {
        this.quizId = quizCounter++;
        this.questions = questions != null ? questions : new ArrayList<>();
        this.studentAnswers = new ArrayList<>();
    }

    public int getQuizId() { return quizId; }
    public void setQuizId(int id) {
        this.quizId = id;
        if(id >= quizCounter) quizCounter = id + 1;
    }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public List<StudentAnswer> getStudentAnswers() { return studentAnswers; }
    public void setStudentAnswers(List<StudentAnswer> answers) { this.studentAnswers = answers; }

    public int getPassingScore() { return passingScore; }
    public void setPassingScore(int score) { this.passingScore = score; }

    // حساب درجة الطالب بالنسبة المئوية
    public int calculateScoreForStudent(Student student) {
        if (questions == null || questions.isEmpty()) return 0;
        int correctCount = 0;
        for(StudentAnswer sa : studentAnswers){
            if(sa.getStudentId() == student.getId() && sa.getSelectedOption() == sa.getQuestion().getCorrectAnswer()){
                correctCount++;
            }
        }
        return (int)((correctCount * 100.0) / questions.size());
    }

    // التحقق إذا نجح الطالب
    public boolean hasStudentPassed(Student student) {
        return calculateScoreForStudent(student) >= passingScore;
    }

    // حساب متوسط جميع الطلاب
    public double getAverageScore() {
        if(studentAnswers == null || studentAnswers.isEmpty()) return 0;
        double total = 0;
        for(StudentAnswer sa : studentAnswers){
            total += sa.getScore();
        }
        return total / studentAnswers.size();
    }
}
