package com.example.ui;

import com.example.models.Course;
import com.example.models.Instructor;
import com.example.models.Lesson;
import com.example.models.Student;
import com.example.services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {
    private Instructor instructor;
    private CourseService courseService;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public InstructorDashboardFrame(Instructor instructor) {
        this.instructor = instructor;
        this.courseService = CourseService.getInstance();

        setTitle("Instructor Dashboard");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));

        // ================= Top Bar (Welcome + Logout) =================
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel welcomeLabel = new JLabel("Welcome, " + instructor.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ================= Search =================
        searchField = new JTextField();
        searchField.setToolTipText("Search courses...");
        mainPanel.add(searchField, BorderLayout.NORTH);

        // ================= Table =================
        String[] columns = {"ID","Title","Students","Manage Lessons","View Students","Insights","Add Quiz","Delete"};
        tableModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row,int column){ return column >= 3; }
        };
        courseTable = new JTable(tableModel);
        mainPanel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // ======= Column Buttons =======
        addButtonToTable("Manage Lessons", courseId -> {
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                if(c.getLessons() == null) c.setLessons(new java.util.ArrayList<>());
                new LessonManagementFrame(c);
            }
        });

        addButtonToTable("View Students", courseId -> {
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                List<Student> students = c.getEnrolledStudents();
                StringBuilder sb = new StringBuilder();
                if(students != null) {
                    for(Student s : students){
                        sb.append(s.getUsername()).append(" - ").append(s.getEmail()).append("\n");
                    }
                }
                JOptionPane.showMessageDialog(this,
                        sb.length() > 0 ? sb.toString() : "No students enrolled yet.",
                        "Enrolled Students for " + c.getTitle(),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        addButtonToTable("Insights", courseId -> {
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                new ChartFrame(c);
            }
        });

        addButtonToTable("Add Quiz", courseId -> {
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                List<Lesson> lessons = c.getLessons();
                if(lessons == null || lessons.isEmpty()){
                    JOptionPane.showMessageDialog(this,"This course has no lessons to add a quiz.","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // عرض اختيار الدرس
                String[] lessonTitles = lessons.stream().map(Lesson::getTitle).toArray(String[]::new);
                String selected = (String) JOptionPane.showInputDialog(
                        this,
                        "Select a lesson to add quiz:",
                        "Select Lesson",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        lessonTitles,
                        lessonTitles[0]
                );

                if(selected != null){
                    Lesson lesson = lessons.stream().filter(l -> l.getTitle().equals(selected)).findFirst().orElse(null);
                    if(lesson != null){
                        new QuizCreationFrame(lesson);
                    }
                }
            }
        });


        addButtonToTable("Delete", courseId -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this course?",
                    "Delete Confirmation", JOptionPane.YES_NO_OPTION);
            if(confirm == JOptionPane.YES_OPTION){
                Course c = courseService.getCourseById(courseId);
                if(c != null){
                    instructor.getCreatedCourses().removeIf(x -> x.getCourseId() == courseId);
                    courseService.deleteCourse(courseId);
                    refreshCourses();
                    JOptionPane.showMessageDialog(this,"Course deleted successfully.");
                }
            }
        });

        // ================= Create Course =================
        JPanel createPanel = new JPanel(new GridLayout(3,2,10,10));
        createPanel.add(new JLabel("Course Title:"));
        JTextField titleField = new JTextField();
        createPanel.add(titleField);

        createPanel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(5,20);
        createPanel.add(new JScrollPane(descArea));

        JButton createBtn = new JButton("Create Course");
        createPanel.add(createBtn);

        createBtn.addActionListener(e->{
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            if(!title.isEmpty() && !desc.isEmpty()){
                Course c = new Course(title, desc, instructor);
                courseService.addCourse(c);
                if(!instructor.getCreatedCourses().contains(c)){
                    instructor.getCreatedCourses().add(c);
                }
                refreshCourses();
                JOptionPane.showMessageDialog(this,"Course created successfully!");
                titleField.setText(""); descArea.setText("");
            } else {
                JOptionPane.showMessageDialog(this,"Title and Description required!","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        // ================= Layout =================
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("My Courses", mainPanel);
        tabbedPane.add("Create Course", createPanel);
        add(tabbedPane, BorderLayout.CENTER);

        refreshCourses();

        // ================= Search Listener =================
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshCourses(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshCourses(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshCourses(searchField.getText()); }
        });

        setVisible(true);
    }

    // ================= Refresh Table =================
    private void refreshCourses(){ refreshCourses(""); }

    private void refreshCourses(String filter){
        tableModel.setRowCount(0);
        List<Course> courses = instructor.getCreatedCourses();
        if(courses == null) return;
        for(Course c : courses){
            if(filter.isEmpty() || c.getTitle().toLowerCase().contains(filter.toLowerCase())){
                int studentCount = (c.getEnrolledStudents() != null) ? c.getEnrolledStudents().size() : 0;
                tableModel.addRow(new Object[]{
                        c.getCourseId(),
                        c.getTitle(),
                        studentCount,
                        "Manage Lessons",
                        "View Students",
                        "Insights",
                        "Add Quiz",
                        "Delete"
                });
            }
        }
    }

    // ================= Button Helpers =================
    private void addButtonToTable(String columnName, ButtonAction action){
        courseTable.getColumn(columnName).setCellRenderer(new ButtonRenderer());
        courseTable.getColumn(columnName).setCellEditor(new ButtonEditor(columnName, action));
    }

    interface ButtonAction{ void action(int id); }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer{
        public ButtonRenderer(){ setOpaque(true);}
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            setText((value==null)?"":value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor{
        protected JButton button;
        private boolean clicked;
        private int id;
        private ButtonAction action;

        public ButtonEditor(String btnText, ButtonAction action){
            super(new JCheckBox());
            this.button = new JButton(btnText);
            this.button.setOpaque(true);
            this.action = action;
            button.addActionListener(e->fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
            try {
                id = (int) table.getValueAt(row,0);
            } catch(Exception ex){
                id = -1;
            }
            clicked=true;
            return button;
        }

        public Object getCellEditorValue(){
            if(clicked && id != -1) action.action(id);
            clicked=false;
            return "";
        }

        public boolean stopCellEditing(){ clicked=false; return super.stopCellEditing(); }
        protected void fireEditingStopped(){ super.fireEditingStopped(); }
    }
}
