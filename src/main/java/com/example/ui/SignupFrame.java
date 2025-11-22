package com.example.ui;

import com.example.services.AuthService;

import javax.swing.*;
import java.awt.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private AuthService authService;

    public SignupFrame() {
        authService = AuthService.getInstance();

        setTitle("Signup");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Role:"));
        roleBox = new JComboBox<>(new String[]{"Student","Instructor"});
        panel.add(roleBox);

        JButton signupBtn = new JButton("Signup");
        panel.add(signupBtn);
        JButton backBtn = new JButton("Back to Login");
        panel.add(backBtn);

        add(panel);

        signupBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = authService.signup(username,email,password,role);
            if(success){
                JOptionPane.showMessageDialog(this,"Signup Successful!");
                dispose();
                new LoginFrame();
            } else {
                JOptionPane.showMessageDialog(this,"Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }
}
