package com.example.ui;

import com.example.models.Course;
import com.example.models.Lesson;
import com.example.services.CourseService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LessonManagementFrame extends JFrame {
    private Course course;
    private CourseService courseService;
    private JTable lessonTable;
    private DefaultTableModel lessonModel;

    public LessonManagementFrame(Course course){
        this.course = course;
        this.courseService = CourseService.getInstance();

        setTitle("Manage Lessons: " + course.getTitle());
        setSize(700,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] columns = {"ID", "Title", "Action"};
        lessonModel = new DefaultTableModel(columns,0){
            public boolean isCellEditable(int row,int column){ return column==2; }
        };

        lessonTable = new JTable(lessonModel);
        lessonTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        lessonTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), (lessonId)->{
            Lesson l = course.getLessons().stream().filter(les->les.getLessonId()==lessonId).findFirst().orElse(null);
            if(l!=null) manageLessonDialog(l);
        }));

        refreshLessons();

        add(new JScrollPane(lessonTable), BorderLayout.CENTER);

        JButton addBtn = new JButton("Add Lesson");
        addBtn.addActionListener(e->addLessonDialog());
        add(addBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void refreshLessons(){
        lessonModel.setRowCount(0);
        if(course.getLessons()==null) course.setLessons(new java.util.ArrayList<>());
        for(Lesson l : course.getLessons()){
            lessonModel.addRow(new Object[]{l.getLessonId(), l.getTitle(), "Edit/Delete"});
        }
    }

    private void addLessonDialog(){
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(5,20);

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Lesson Title:")); panel.add(titleField);
        panel.add(new JLabel("Lesson Content:")); panel.add(new JScrollPane(contentArea));

        int res = JOptionPane.showConfirmDialog(this, panel, "Add Lesson", JOptionPane.OK_CANCEL_OPTION);
        if(res==JOptionPane.OK_OPTION){
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

        JPanel panel = new JPanel(new GridLayout(0,1,5,5));
        panel.add(new JLabel("Lesson Title:")); panel.add(titleField);
        panel.add(new JLabel("Lesson Content:")); panel.add(new JScrollPane(contentArea));

        Object[] options = {"Save","Delete","Cancel"};
        int res = JOptionPane.showOptionDialog(this,panel,"Edit Lesson",
                JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);

        if(res==JOptionPane.YES_OPTION){
            lesson.setTitle(titleField.getText());
            lesson.setContent(contentArea.getText());
            courseService.saveCourses();
            refreshLessons();
        } else if(res==JOptionPane.NO_OPTION){
            int confirm = JOptionPane.showConfirmDialog(this,"Confirm delete?","Confirm",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION){
                course.getLessons().remove(lesson);
                courseService.saveCourses();
                refreshLessons();
            }
        }
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer{
        public ButtonRenderer(){ setOpaque(true);}
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column){
            setText((value==null)?"":value.toString()); return this;
        }
    }

    interface ButtonAction{ void action(int id);}
    class ButtonEditor extends DefaultCellEditor{
        protected JButton button; private boolean clicked; private int id; private ButtonAction action;
        public ButtonEditor(JCheckBox checkBox, ButtonAction action){
            super(checkBox); button = new JButton(); button.setOpaque(true); button.setText("Edit/Delete");
            this.action = action; button.addActionListener(e->fireEditingStopped());
        }
        public Component getTableCellEditorComponent(JTable table,Object value,boolean isSelected,int row,int column){
            id = (int)table.getValueAt(row,0); clicked=true; return button;
        }
        public Object getCellEditorValue(){ if(clicked) action.action(id); clicked=false; return ""; }
        public boolean stopCellEditing(){ clicked=false; return super.stopCellEditing(); }
        protected void fireEditingStopped(){ super.fireEditingStopped(); }
    }
}
