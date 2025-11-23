package com.example.ui;

import com.example.models.*;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;

public class LessonViewerFrame extends JFrame {
    private Lesson lesson;
    private Student student;
    private Course course;
    private CourseService courseService;
    private StudentDashboardFrame dashboard;

    public LessonViewerFrame(Lesson lesson, Student student, Course course, StudentDashboardFrame dashboard){

        if(!lesson.isUnlocked(student, course)){
            JOptionPane.showMessageDialog(null, "Complete the previous quiz to unlock this lesson.", "Locked", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        this.lesson = lesson;
        this.student = student;
        this.course = course;
        this.dashboard = dashboard;
        this.courseService = CourseService.getInstance();

        setTitle("Lesson: " + lesson.getTitle());
        setSize(700,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // محتوى الدرس
        JTextArea contentArea = new JTextArea(lesson.getContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        mainPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // أزرار الإجراءات
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton markBtn = new JButton("Mark Lesson as Completed");
        markBtn.addActionListener(e -> markCompleted());
        buttonPanel.add(markBtn);

        // إذا فيه Quiz مرتبط بالدرس
        if(lesson.getQuiz() != null){
            JButton quizBtn = new JButton("Take Quiz");
            quizBtn.addActionListener(e -> new QuizFrame(lesson.getQuiz(), student, lesson, course, dashboard));
            buttonPanel.add(quizBtn);
        }

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void markCompleted(){
        if(student.markLessonCompleted(course, lesson)){
            courseService.saveCourses();
            JOptionPane.showMessageDialog(this, "Lesson marked as completed!");
            dashboard.refreshEnrolledCourses(); // تحديث الجدول مباشرة
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lesson already completed!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
