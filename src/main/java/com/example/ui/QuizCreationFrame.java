package com.example.ui;

import com.example.models.Lesson;
import com.example.models.Question;
import com.example.models.Quiz;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizCreationFrame extends JFrame {

    private Lesson lesson;  // بدل Course
    private JTextField quizTitleField;
    private JTextField questionField;
    private List<JTextField> optionFields;
    private JSpinner correctAnswerSpinner;
    private List<Question> questions = new ArrayList<>();

    public QuizCreationFrame(Lesson lesson) {
        this.lesson = lesson;

        setTitle("Add Quiz to Lesson: " + lesson.getTitle());
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Quiz Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(new JLabel("Quiz Title:"), BorderLayout.NORTH);
        quizTitleField = new JTextField();
        titlePanel.add(quizTitleField, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Question Input
        JPanel questionPanel = new JPanel(new GridLayout(7,1,5,5));
        questionPanel.setBorder(BorderFactory.createTitledBorder("Add Question"));

        questionField = new JTextField();
        questionPanel.add(new JLabel("Question:"));
        questionPanel.add(questionField);

        optionFields = new ArrayList<>();
        for(int i=0;i<4;i++){
            JTextField opt = new JTextField();
            optionFields.add(opt);
            questionPanel.add(new JLabel("Option " + (i+1) + ":"));
            questionPanel.add(opt);
        }

        questionPanel.add(new JLabel("Correct Answer (0-3):"));
        correctAnswerSpinner = new JSpinner(new SpinnerNumberModel(0,0,3,1));
        questionPanel.add(correctAnswerSpinner);

        panel.add(questionPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addQuestionBtn = new JButton("Add Question");
        JButton finishBtn = new JButton("Finish Quiz");

        btnPanel.add(addQuestionBtn);
        btnPanel.add(finishBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Add Question Logic
        addQuestionBtn.addActionListener(e -> {
            String qText = questionField.getText().trim();
            List<String> opts = new ArrayList<>();
            for(JTextField tf : optionFields) opts.add(tf.getText().trim());

            int correct = (int) correctAnswerSpinner.getValue();

            boolean valid = !qText.isEmpty();
            for(String s : opts) if(s.isEmpty()) valid = false;

            if(valid){
                questions.add(new Question(qText, opts, correct));
                questionField.setText("");
                for(JTextField tf : optionFields) tf.setText("");
                correctAnswerSpinner.setValue(0);
                JOptionPane.showMessageDialog(this,"Question added!");
            } else {
                JOptionPane.showMessageDialog(this,"All fields must be filled.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // Finish Quiz Logic
        finishBtn.addActionListener(e -> {
            String quizTitle = quizTitleField.getText().trim();
            if(quizTitle.isEmpty()) quizTitle = "Untitled Quiz";

            if(questions.isEmpty()){
                JOptionPane.showMessageDialog(this,"Add at least one question.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

            Quiz quiz = new Quiz(quizTitle, questions);
            lesson.setQuiz(quiz);  // ربط الكويز بالدرس مباشرة
            JOptionPane.showMessageDialog(this,"Quiz added to lesson!");
            dispose();
        });


        add(panel);
        setVisible(true);
    }
}
