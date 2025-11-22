package com.example.ui;

import com.example.models.Course;
import com.example.models.Instructor;
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
        setSize(1000, 600);
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
            new LoginFrame();
        });

        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ================= Search =================
        searchField = new JTextField();
        searchField.setToolTipText("Search courses...");
        mainPanel.add(searchField, BorderLayout.NORTH);

        // ================= Table =================
        String[] columns = {"ID","Title","Students","Manage Lessons","View Students"};
        tableModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row,int column){ return column>=3; }
        };
        courseTable = new JTable(tableModel);

        // Manage Lessons Button
        courseTable.getColumn("Manage Lessons").setCellRenderer(new ButtonRenderer());
        courseTable.getColumn("Manage Lessons").setCellEditor(new ButtonEditor("Manage Lessons", courseId->{
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                if(c.getLessons()==null) c.setLessons(new java.util.ArrayList<>());
                new LessonManagementFrame(c);
            }
        }));

        // View Enrolled Students Button
        courseTable.getColumn("View Students").setCellRenderer(new ButtonRenderer());
        courseTable.getColumn("View Students").setCellEditor(new ButtonEditor("View Students", courseId->{
            Course c = courseService.getCourseById(courseId);
            if(c != null){
                List<Student> students = c.getEnrolledStudents();
                StringBuilder sb = new StringBuilder();
                for(Student s : students){
                    sb.append(s.getUsername()).append(" - ").append(s.getEmail()).append("\n");
                }
                JOptionPane.showMessageDialog(this,
                        sb.length()>0?sb.toString():"No students enrolled yet.",
                        "Enrolled Students for " + c.getTitle(),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }));

        mainPanel.add(new JScrollPane(courseTable), BorderLayout.CENTER);

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
            String title = titleField.getText();
            String desc = descArea.getText();
            if(!title.isEmpty() && !desc.isEmpty()){
                Course c = new Course(title, desc, instructor);
                courseService.addCourse(c);
                instructor.getCreatedCourses().add(c);
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
        searchField.addKeyListener(new java.awt.event.KeyAdapter(){
            public void keyReleased(java.awt.event.KeyEvent e){
                refreshCourses(searchField.getText());
            }
        });

        setVisible(true);
    }

    private void refreshCourses(){ refreshCourses(""); }

    private void refreshCourses(String filter){
        tableModel.setRowCount(0);
        List<Course> courses = instructor.getCreatedCourses();
        for(Course c : courses){
            if(filter.isEmpty() || c.getTitle().toLowerCase().contains(filter.toLowerCase())){
                tableModel.addRow(new Object[]{
                        c.getCourseId(),
                        c.getTitle(),
                        c.getEnrolledStudents()!=null?c.getEnrolledStudents().size():0,
                        "Manage Lessons",
                        "View Students"
                });
            }
        }
    }

    // ================= Table Button Renderer =================
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer{
        public ButtonRenderer(){ setOpaque(true);}
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            setText((value==null)?"":value.toString());
            return this;
        }
    }

    interface ButtonAction{ void action(int id);}

    class ButtonEditor extends DefaultCellEditor{
        protected JButton button; private boolean clicked; private int id; private ButtonAction action;

        public ButtonEditor(String btnText, ButtonAction action){
            super(new JCheckBox());
            this.button = new JButton(btnText);
            this.button.setOpaque(true);
            this.action = action;
            button.addActionListener(e->fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
            id = (int)table.getValueAt(row,0);
            clicked=true;
            return button;
        }

        public Object getCellEditorValue(){
            if(clicked) action.action(id);
            clicked=false;
            return "";
        }

        public boolean stopCellEditing(){ clicked=false; return super.stopCellEditing(); }
        protected void fireEditingStopped(){ super.fireEditingStopped(); }
    }
}
