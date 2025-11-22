package com.example.models;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> options;
    private int correctAnswer;

    public Question() {}

    public Question(String questionText, List<String> options, int correctAnswer) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public int getCorrectAnswer() { return correctAnswer; }
}