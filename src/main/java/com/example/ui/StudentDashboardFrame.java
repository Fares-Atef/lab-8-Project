package com.example.ui;

import com.example.models.Course;
import com.example.models.Student;
import com.example.services.CourseService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private Student student;
    private CourseService courseService;

    private JTable availableTable;
    private JTable enrolledTable;
    private DefaultTableModel availableModel;
    private DefaultTableModel enrolledModel;
    private JTextField searchField;

    public StudentDashboardFrame(Student student) {
        this.student = student;
        this.courseService = CourseService.getInstance();

        setTitle("Student Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getUsername());
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose(); // اغلاق الـ Dashboard الحالي
            new LoginFrame(); // فتح Login Frame جديد
        });
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= Available Courses =================
        JPanel availablePanel = new JPanel(new BorderLayout(10,10));
        searchField = new JTextField();
        searchField.setToolTipText("Search courses...");
        availablePanel.add(searchField, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Instructor", "Action"};
        availableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        availableTable = new JTable(availableModel);
        availableTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        availableTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "Enroll", courseId -> {
            if(courseId == -1) return; // حماية من Row فارغ
            Course c = courseService.getCourseById(courseId);
            if(c != null) student.enrollCourse(c);
            refreshEnrolledCourses();
            refreshAvailableCourses();
            courseService.saveCourses();
        }));
        availablePanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        tabbedPane.add("Available Courses", availablePanel);

        // ================= Enrolled Courses =================
        JPanel enrolledPanel = new JPanel(new BorderLayout(10,10));
        String[] enrolledCols = {"ID", "Title", "Instructor", "Progress", "Action"};
        enrolledModel = new DefaultTableModel(enrolledCols, 0) {
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };
        enrolledTable = new JTable(enrolledModel);
        enrolledTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        enrolledTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "View", courseId -> {
            if(courseId == -1) return;
            Course c = courseService.getCourseById(courseId);
            if(c != null) new LessonListFrame(student, c, this);
        }));

        enrolledTable.getColumn("Progress").setCellRenderer(new ProgressRenderer());
        enrolledPanel.add(new JScrollPane(enrolledTable), BorderLayout.CENTER);
        tabbedPane.add("Enrolled Courses", enrolledPanel);

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);

        // ================= Search =================
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                refreshAvailableCourses(searchField.getText());
            }
        });

        refreshAvailableCourses();
        refreshEnrolledCourses();
    }

    public void refreshAvailableCourses() { refreshAvailableCourses(""); }

    private void refreshAvailableCourses(String filter) {
        availableModel.setRowCount(0);
        List<Course> courses = courseService.getAvailableCourses(student);
        if(courses == null) return;
        for(Course c : courses){
            if(filter.isEmpty() || c.getTitle().toLowerCase().contains(filter.toLowerCase())){
                String instName = (c.getInstructor() != null) ? c.getInstructor().getUsername() : "Unknown";
                availableModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), instName, "Enroll"});
            }
        }
    }

    public void refreshEnrolledCourses() {
        enrolledModel.setRowCount(0);
        List<Course> courses = student.getEnrolledCourses();
        if(courses == null) return;
        for(Course c : courses){
            int progress = student.getProgress(c);
            String instName = (c.getInstructor() != null) ? c.getInstructor().getUsername() : "Unknown";
            enrolledModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), instName, progress, "View"});
        }
    }

    // ================= Renderers & Editors =================
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    interface ButtonAction { void action(int courseId); }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int courseId;
        private boolean clicked;
        private ButtonAction action;

        public ButtonEditor(JCheckBox checkBox, String btnText, ButtonAction action){
            super(checkBox);
            this.button = new JButton(btnText);
            this.button.setOpaque(true);
            this.action = action;
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
            if(row >= 0 && row < table.getRowCount()){
                Object idObj = table.getValueAt(row,0);
                if(idObj instanceof Integer){
                    courseId = (Integer) idObj;
                } else {
                    courseId = -1;
                }
            } else {
                courseId = -1;
            }
            clicked = true;
            return button;
        }

        public Object getCellEditorValue(){
            if(clicked && courseId != -1) action.action(courseId);
            clicked = false;
            return "";
        }
    }

    class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressRenderer() { setStringPainted(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            int val = (value instanceof Integer) ? (Integer)value : 0;
            setValue(val);
            setString(val + "%");
            return this;
        }
    }
}
