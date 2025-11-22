package com.example.ui;

import com.example.models.Instructor;
import com.example.models.Course;
import com.example.services.CourseService;

import javax.swing.*;
import java.awt.*;

public class CreateCourseFrame extends JFrame {
    public CreateCourseFrame(Instructor instructor) {
        CourseService courseService = CourseService.getInstance();

        setTitle("Create New Course");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        panel.add(new JLabel("Course Title:"));
        JTextField titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(5,20);
        panel.add(new JScrollPane(descArea));

        JButton createBtn = new JButton("Create");
        panel.add(createBtn);

        createBtn.addActionListener(e -> {
            String title = titleField.getText();
            String desc = descArea.getText();
            if(!title.isEmpty() && !desc.isEmpty()){
                Course course = new Course(title, desc, instructor);
                courseService.addCourse(course);
                instructor.getCreatedCourses().add(course);
                courseService.saveCourses();
                JOptionPane.showMessageDialog(this, "Course created successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Title and Description required!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel);
        setVisible(true);
    }
}
