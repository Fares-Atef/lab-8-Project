package com.example.ui;

import com.example.models.Course;
import com.example.models.Instructor;
import com.example.services.CourseService;
import com.example.database.JsonDatabaseManager;

import javax.swing.*;
import java.awt.*;

public class EditCourseFrame extends JFrame {
    public EditCourseFrame(Course course) {
        if (course == null) {
            JOptionPane.showMessageDialog(null, "Error: Course is null!");
            dispose();
            return;
        }

        CourseService courseService = CourseService.getInstance();

        setTitle("Edit Course: " + course.getTitle());
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        panel.add(new JLabel("Course Title:"));
        JTextField titleField = new JTextField(course.getTitle());
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(course.getDescription(), 5, 20);
        panel.add(new JScrollPane(descArea));

        JButton saveBtn = new JButton("Save");
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();

            if(title.isEmpty() || desc.isEmpty()){
                JOptionPane.showMessageDialog(this, "Title and Description are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ================= Update Course =================
            course.setTitle(title);
            course.setDescription(desc);

            // ================= Ensure Instructor Link =================
            Instructor inst = course.getInstructor();
            if(inst != null && !inst.getCreatedCourses().contains(course)){
                inst.getCreatedCourses().add(course);
            }

            // ================= Save Changes =================
            courseService.saveCourses();
            JsonDatabaseManager.getInstance().saveCourses();

            JOptionPane.showMessageDialog(this, "Course updated successfully!");
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}
