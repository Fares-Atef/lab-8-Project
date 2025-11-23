package com.example.ui;

import com.example.models.*;
import com.example.services.CourseService;
import com.example.database.JsonDatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizFrame extends JFrame {
    private Quiz quiz;
    private Student student;
    private Lesson lesson;
    private Course course;
    private StudentDashboardFrame dashboard;

    private JPanel mainPanel;
    private JButton submitBtn;
    private ButtonGroup[] buttonGroups;

    public QuizFrame(Quiz quiz, Student student, Lesson lesson, Course course, StudentDashboardFrame dashboard){
        this.quiz = quiz;
        this.student = student;
        this.lesson = lesson;
        this.course = course;
        this.dashboard = dashboard;

        setTitle("Quiz: " + lesson.getTitle());
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane, BorderLayout.CENTER);

        List<Question> questions = quiz.getQuestions();
        buttonGroups = new ButtonGroup[questions.size()];

        for(int i=0; i<questions.size(); i++){
            Question q = questions.get(i);
            JPanel qPanel = new JPanel();
            qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
            qPanel.setBorder(BorderFactory.createTitledBorder("Question "+(i+1)));
            qPanel.add(new JLabel(q.getQuestionText()));

            ButtonGroup group = new ButtonGroup();
            buttonGroups[i] = group;
            List<String> opts = q.getOptions();
            for(int j=0; j<opts.size(); j++){
                JRadioButton rb = new JRadioButton(opts.get(j));
                rb.setActionCommand(""+j);
                group.add(rb);
                qPanel.add(rb);
            }
            mainPanel.add(qPanel);
        }

        submitBtn = new JButton("Submit Quiz");
        submitBtn.addActionListener(e -> submitQuiz());
        add(submitBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void submitQuiz(){
        List<Question> questions = quiz.getQuestions();
        int score = 0;

        for(int i=0; i<questions.size(); i++){
            Question q = questions.get(i);
            ButtonGroup g = buttonGroups[i];
            if(g.getSelection() != null){
                int selected = Integer.parseInt(g.getSelection().getActionCommand());
                StudentAnswer sa = new StudentAnswer(student.getId(), q, selected);
                quiz.getStudentAnswers().add(sa);
                if(selected == q.getCorrectAnswer()) score++;
            }
        }

        int percent = (int)((score * 100.0) / questions.size());
        student.saveQuizScore(lesson.getLessonId(), percent);

        if(quiz.hasStudentPassed(student)){
            student.markQuizPassed(quiz, course);
        }

        JsonDatabaseManager.getInstance().saveUsers();
        CourseService.getInstance().saveCourses();

        // عرض النتيجة مع الإجابات الصحيحة
        StringBuilder result = new StringBuilder();
        result.append("Your Score: ").append(percent).append("%\n\nCorrect Answers:\n");
        for(int i=0;i<questions.size();i++){
            Question q = questions.get(i);
            result.append("Q").append(i+1).append(": ").append(q.getOptions().get(q.getCorrectAnswer())).append("\n");
        }

        JOptionPane.showMessageDialog(this, result.toString());
        dashboard.refreshEnrolledCourses();
        dispose();
    }
}
