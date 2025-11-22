package com.example.ui;

import com.example.models.Course;
import com.example.models.Lesson;
import com.example.models.Student;

import javax.swing.*;
import java.awt.*;

public class LessonListFrame extends JFrame {
    private Student student;
    private Course course;
    private StudentDashboardFrame dashboard;

    private JList<String> lessonJList;
    private DefaultListModel<String> listModel;

    public LessonListFrame(Student student, Course course, StudentDashboardFrame dashboard){
        this.student = student;
        this.course = course;
        this.dashboard = dashboard;

        setTitle("Lessons in Course: " + course.getTitle());
        setSize(600,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        listModel = new DefaultListModel<>();
        lessonJList = new JList<>(listModel);
        lessonJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        refreshList();

        JButton viewBtn = new JButton("View / Mark Completed");
        viewBtn.addActionListener(e -> {
            int idx = lessonJList.getSelectedIndex();
            if(idx >= 0){
                Lesson l = course.getLessons().get(idx);
                new LessonViewerFrame(l, student, course, dashboard);
            }
        });

        add(new JScrollPane(lessonJList), BorderLayout.CENTER);
        add(viewBtn, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void refreshList(){
        listModel.clear();
        for(Lesson l : course.getLessons()){
            listModel.addElement(l.getTitle());
        }
    }
}
