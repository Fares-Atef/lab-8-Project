package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private static int lessonCounter = 1;

    private int lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private Quiz quiz;

    public Lesson() {                   // ★ مهم لـ Gson
        this.lessonId = lessonCounter++;
        this.resources = new ArrayList<>();
    }

    public Lesson(String title, String content){
        this.lessonId = lessonCounter++;
        this.title = (title != null) ? title : "Untitled Lesson";
        this.content = (content != null) ? content : "";
        this.resources = new ArrayList<>();
    }

    // ==== Setters needed for JSON ====
    public void setLessonId(int id){
        this.lessonId = id;
        if(id >= lessonCounter)
            lessonCounter = id + 1;    // ★ يمنع تكرار IDs بعد reload
    }

    public void setResources(List<String> resources){
        this.resources = (resources != null) ? resources : new ArrayList<>();
    }

    // ==== Resources ====
    public void addResource(String resource){
        if(resource != null){
            if(resources == null) resources = new ArrayList<>();
            resources.add(resource);
        }
    }

    public List<String> getResources(){
        if(resources == null) resources = new ArrayList<>();
        return resources;
    }

    // ==== Getters & Setters ====
    public int getLessonId() { return lessonId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = (title != null) ? title : this.title; }
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = (content != null) ? content : this.content; }

    public static void setLessonCounter(int value) { lessonCounter = value; }
}
