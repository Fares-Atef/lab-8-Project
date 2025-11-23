package com.example.ui;

import com.example.models.Admin;
import com.example.models.Course;
import com.example.services.AdminService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private AdminService adminService;
    private Admin loggedAdmin;
    private JTable pendingTable;
    private DefaultTableModel pendingModel;

    public AdminDashboardFrame(Admin adminUser) {

        this.adminService = AdminService.getInstance();
        this.loggedAdmin = adminUser;

        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= TOP BAR =================
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + adminUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            dispose(); // اقفل Frame الحالية
            new LoginFrame(); // افتح شاشة اللوجين
        });

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ================= Pending Courses Table =================
        String[] columns = {"ID", "Title", "Instructor", "Description", "Status", "Action"};
        pendingModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return column == 5; }
        };
        pendingTable = new JTable(pendingModel);
        pendingTable.getColumn("Action").setCellRenderer(new ButtonRenderer());

        JScrollPane scrollPane = new JScrollPane(pendingTable);
        add(scrollPane, BorderLayout.CENTER);

        if (pendingTable.isEditing()) {
            pendingTable.getCellEditor().stopCellEditing();
        }

        refreshPendingCourses();

        setVisible(true);
    }


    // ================= LOAD PENDING =================
    private void refreshPendingCourses() {
        pendingModel.setRowCount(0);

        List<Course> courses = adminService.getPendingCourses();
        if (courses == null || courses.isEmpty()) return;

        for (Course c : courses) {
            pendingModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getTitle(),
                    (c.getInstructor() != null) ? c.getInstructor().getUsername() : "Unknown",
                    c.getDescription(),
                    c.getStatus(),
                    "Review"
            });
        }

        if (pendingTable.getRowCount() > 0) {
            pendingTable.getColumn("Action").setCellEditor(
                    new ButtonEditor(new JCheckBox(), courseId -> showApproveDeclineDialog(courseId))
            );
        }
    }


    // ================= REVIEW DIALOG =================
    private void showApproveDeclineDialog(int courseId) {
        Course c = adminService.getPendingCourses()
                .stream()
                .filter(x -> x.getCourseId() == courseId)
                .findFirst()
                .orElse(null);

        if (c == null) return;

        String[] options = {"Approve", "Reject"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Review Course: " + c.getTitle(),
                "Course Approval",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            adminService.approveCourse(courseId, loggedAdmin);
            JOptionPane.showMessageDialog(this, "Course approved successfully.");

        } else if (choice == 1) {
            String reason = JOptionPane.showInputDialog(
                    this,
                    "Enter rejection reason:",
                    "Rejection Reason",
                    JOptionPane.PLAIN_MESSAGE
            );

            if (reason != null && !reason.trim().isEmpty()) {
                adminService.rejectCourse(courseId, loggedAdmin, reason);
                JOptionPane.showMessageDialog(this, "Course rejected.");
            } else {
                JOptionPane.showMessageDialog(this, "Rejection cancelled (reason required).");
            }
        }

        if (pendingTable.isEditing()) {
            pendingTable.getCellEditor().stopCellEditing();
        }
        refreshPendingCourses();
    }


    // ================= Renderers & Editors =================
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column
        ) {
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

        public ButtonEditor(JCheckBox checkBox, ButtonAction action) {
            super(checkBox);
            this.button = new JButton("Review");
            this.button.setOpaque(true);
            this.action = action;

            button.addActionListener(e -> {
                if (clicked && courseId != -1)
                    action.action(courseId);

                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            courseId = (int) table.getValueAt(row, 0);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            clicked = false;
            return "Review";
        }
    }
}
