package com.example.ui;

import com.example.models.*;
import com.example.services.CourseService;
import com.example.utils.CertificateGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private Student student;
    private CourseService courseService;

    private JTable availableTable;
    private JTable enrolledTable;
    private JTable certificateTable;
    private DefaultTableModel availableModel;
    private DefaultTableModel enrolledModel;
    private DefaultTableModel certificateModel;
    private JTextField searchField;

    public StudentDashboardFrame(Student student) {
        this.student = student;
        this.courseService = CourseService.getInstance();

        setTitle("Student Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // ================= Top Panel =================
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + student.getUsername());
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
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
        availablePanel.add(new JScrollPane(availableTable), BorderLayout.CENTER);
        tabbedPane.add("Available Courses", availablePanel);

        // ================= Enrolled Courses =================
        JPanel enrolledPanel = new JPanel(new BorderLayout(10,10));
        String[] enrolledCols = {"ID", "Title", "Instructor", "Progress", "Action"};
        enrolledModel = new DefaultTableModel(enrolledCols, 0) {
            public boolean isCellEditable(int row, int column) { return column == 4; }
        };
        enrolledTable = new JTable(enrolledModel);
        enrolledTable.getColumn("Progress").setCellRenderer(new ProgressRenderer());
        enrolledPanel.add(new JScrollPane(enrolledTable), BorderLayout.CENTER);
        tabbedPane.add("Enrolled Courses", enrolledPanel);

        // ================= Certificates =================
        JPanel certPanel = new JPanel(new BorderLayout(10,10));
        String[] certCols = {"Certificate ID", "Course Title", "Issue Date", "Action"};
        certificateModel = new DefaultTableModel(certCols, 0) {
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        certificateTable = new JTable(certificateModel);
        certPanel.add(new JScrollPane(certificateTable), BorderLayout.CENTER);
        tabbedPane.add("Certificates", certPanel);

        add(tabbedPane, BorderLayout.CENTER);


        // ================= Search Listener =================
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshAvailableCourses(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshAvailableCourses(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshAvailableCourses(searchField.getText()); }
        });

        // ================= Refresh All Tables =================

        refreshAvailableCourses();
        refreshEnrolledCourses();
        refreshCertificates();

        setVisible(true);
    }

    // ================= Refresh Available Courses =================
    public void refreshAvailableCourses() {
        if (availableTable.isEditing()) {
            availableTable.getCellEditor().stopCellEditing();
        }
        refreshAvailableCourses(""); }
    private void refreshAvailableCourses(String filter) {

        availableModel.setRowCount(0);
        List<Course> courses = courseService.getAvailableCourses(student);
        if(courses == null) return;
        if (availableTable.isEditing()) {
            availableTable.getCellEditor().stopCellEditing();
        }

        for(Course c : courses){
            if(filter.isEmpty() || c.getTitle().toLowerCase().contains(filter.toLowerCase())){
                String instName = (c.getInstructor() != null) ? c.getInstructor().getUsername() : "Unknown";
                availableModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), instName, "Enroll"});
            }
        }

        if(availableTable.getRowCount() > 0){
            availableTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
            availableTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "Enroll", courseId -> {
                Course c = courseService.getCourseById(courseId);
                if(c != null && !student.getEnrolledCourses().contains(c)){
                    if(c.getStatus() != CourseStatus.APPROVED){
                        JOptionPane.showMessageDialog(this,
                                "This course is not approved yet.",
                                "Not Allowed",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    student.enrollCourse(c);
                    courseService.saveCourses();
                    refreshEnrolledCourses();
                    refreshAvailableCourses();
                }
            }));
        }
    }

    // ================= Refresh Enrolled Courses =================
    public void refreshEnrolledCourses() {
        if (enrolledTable.isEditing()) {
            enrolledTable.getCellEditor().stopCellEditing();
        }
        enrolledModel.setRowCount(0);
        List<Course> courses = student.getEnrolledCourses();
        if(courses == null) return;

        for(Course c : courses){
            int progress = student.getProgress(c);
            String instName = (c.getInstructor() != null) ? c.getInstructor().getUsername() : "Unknown";
            enrolledModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), instName, progress, "View"});
        }

        if(enrolledTable.getRowCount() > 0){
            enrolledTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
            enrolledTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "View", courseId -> {
                Course c = courseService.getCourseById(courseId);
                if(c != null) new LessonListFrame(student, c, this);
            }));
        }
    }

    // ================= Refresh Certificates =================
    public void refreshCertificates() {
        // لو الجدول في وضع تحرير، نوقفه
        if (certificateTable.isEditing()) {
            certificateTable.getCellEditor().stopCellEditing();
        }

        // نمسح كل الصفوف القديمة
        certificateModel.setRowCount(0);

        List<Certificate> certs = student.getCertificates();
        if (certs == null || certs.isEmpty()) return;

        // نضيف كل الشهادات للجدول
        for (Certificate cert : certs) {
            if(cert == null) continue;
            Course c = courseService.getCourseById(cert.getCourseId());
            String courseTitle = (c != null) ? c.getTitle() : "Unknown";
            certificateModel.addRow(new Object[]{
                    cert.getCertificateId(),
                    courseTitle,
                    cert.getIssueDate(),
                    "View/Download"
            });
        }

        // إعداد زر View/Download
        if (certificateTable.getRowCount() > 0) {
            certificateTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
            certificateTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "View/Download", row -> {
                if (row < 0 || row >= student.getCertificates().size()) return;

                Certificate cert = student.getCertificates().get(row);
                if (cert == null) return;

                Course course = courseService.getCourseById(cert.getCourseId());
                if (course != null) {
                    // هنا فقط نولد PDF بدون إنشاء شهادة جديدة
                    CertificateGenerator.generatePDF(student, course);
                }
            }));
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

    interface ButtonAction { void action(int value); }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int value;
        private boolean clicked;
        private ButtonAction action;

        public ButtonEditor(JCheckBox checkBox, String btnText, ButtonAction action){
            super(checkBox);
            this.button = new JButton(btnText);
            this.button.setOpaque(true);
            this.action = action;
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object valueObj, boolean isSelected, int row, int column){
            if(button.getText().equals("View/Download")){
                value = row;
            } else {
                Object idObj = table.getValueAt(row, 0);
                value = (idObj instanceof Integer) ? (Integer) idObj : -1;
            }
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue(){
            if(clicked && value != -1){
                try { action.action(value); }
                catch(Exception e){ JOptionPane.showMessageDialog(null, "Error: " + e.getMessage()); }
            }
            clicked = false;
            return "";
        }

        @Override
        public boolean stopCellEditing() { clicked = false; return super.stopCellEditing(); }
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
