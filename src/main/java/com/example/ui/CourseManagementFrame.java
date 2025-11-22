package com.example.ui;

import com.example.models.Course;
import com.example.models.Instructor;
import com.example.models.Lesson;
import com.example.services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseManagementFrame extends JFrame {
    private Course course;
    private Instructor instructor;
    private CourseService courseService;

    private JTable lessonTable;
    private DefaultTableModel lessonModel;

    public CourseManagementFrame(Course course, Instructor instructor) {
        this.course = course;
        this.instructor = instructor;
        this.courseService = CourseService.getInstance();

        setTitle("Manage Course: " + course.getTitle());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // ================= Lessons Tab =================
        JPanel lessonPanel = new JPanel(new BorderLayout(10,10));
        String[] columns = {"ID", "Title", "Action"};
        lessonModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return column == 2; }
        };
        lessonTable = new JTable(lessonModel);
        lessonTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        lessonTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "Edit/Delete", (lessonId) -> {
            Lesson l = course.getLessons().stream().filter(les -> les.getLessonId() == lessonId).findFirst().orElse(null);
            if(l != null) manageLessonDialog(l);
        }));
        refreshLessons();
        lessonPanel.add(new JScrollPane(lessonTable), BorderLayout.CENTER);

        JButton addLessonBtn = new JButton("Add Lesson");
        addLessonBtn.addActionListener(e -> addLessonDialog());
        lessonPanel.add(addLessonBtn, BorderLayout.SOUTH);

        tabbedPane.add("Lessons", lessonPanel);

        // ================= Students Tab =================
        JPanel studentPanel = new JPanel(new BorderLayout(10,10));
        DefaultTableModel studentModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"},0);
        JTable studentTable = new JTable(studentModel);
        List<com.example.models.Student> students = course.getEnrolledStudents();
        for(com.example.models.Student s: students){
            studentModel.addRow(new Object[]{s.getUserId(), s.getUsername(), s.getEmail()});
        }
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        tabbedPane.add("Enrolled Students", studentPanel);

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void refreshLessons() {
        lessonModel.setRowCount(0);
        for(Lesson l : course.getLessons()){
            lessonModel.addRow(new Object[]{l.getLessonId(), l.getTitle(), "Edit/Delete"});
        }
    }

    private void addLessonDialog(){
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(5,20);
        JScrollPane scroll = new JScrollPane(contentArea);

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Lesson Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Lesson Content:"));
        panel.add(scroll);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Lesson", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            String title = titleField.getText();
            String content = contentArea.getText();
            if(!title.isEmpty() && !content.isEmpty()){
                Lesson l = new Lesson(title, content);
                course.getLessons().add(l);
                courseService.saveCourses();
                refreshLessons();
            }
        }
    }

    private void manageLessonDialog(Lesson lesson){
        JTextField titleField = new JTextField(lesson.getTitle());
        JTextArea contentArea = new JTextArea(lesson.getContent(),5,20);
        JScrollPane scroll = new JScrollPane(contentArea);

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Lesson Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Lesson Content:"));
        panel.add(scroll);

        Object[] options = {"Save","Delete","Cancel"};
        int result = JOptionPane.showOptionDialog(this,panel,"Edit Lesson",JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
        if(result == JOptionPane.YES_OPTION){
            lesson.setTitle(titleField.getText());
            lesson.setContent(contentArea.getText());
            courseService.saveCourses();
            refreshLessons();
        } else if(result == JOptionPane.NO_OPTION){
            int confirm = JOptionPane.showConfirmDialog(this,"Are you sure to delete this lesson?","Confirm",JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                course.getLessons().remove(lesson);
                courseService.saveCourses();
                refreshLessons();
            }
        }
    }

    // ================= Button Renderer/Editor =================
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(){ setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            setText((value==null)?"":value.toString());
            return this;
        }
    }

    interface ButtonAction { void action(int id); }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private boolean clicked;
        private int id;
        private ButtonAction action;

        public ButtonEditor(JCheckBox checkBox, String text, ButtonAction action){
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setText(text);
            this.action = action;
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
            id = (int)table.getValueAt(row,0);
            clicked = true;
            return button;
        }

        public Object getCellEditorValue(){
            if(clicked) action.action(id);
            clicked = false;
            return "";
        }
        public boolean stopCellEditing(){ clicked=false; return super.stopCellEditing(); }
        protected void fireEditingStopped(){ super.fireEditingStopped(); }
    }
}
