package com.example.ui;

import com.example.models.User;
import com.example.models.Student;
import com.example.models.Instructor;
import com.example.services.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private AuthService authService;

    public LoginFrame() {
        authService = AuthService.getInstance();

        setTitle("Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        panel.add(loginBtn);
        JButton signupBtn = new JButton("Signup");
        panel.add(signupBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            User user = authService.login(email, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();

                if (user instanceof Instructor) {
                    new InstructorDashboardFrame((Instructor) user);
                } else if (user instanceof Student) {
                    new StudentDashboardFrame((Student) user);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupBtn.addActionListener(e -> {
            dispose();
            new SignupFrame();
        });

        setVisible(true);
    }
}
