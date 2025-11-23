package com.example.utils;

import com.example.models.Certificate;
import com.example.models.Student;
import com.example.models.Course;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class CertificateGenerator {

    public static Certificate generatePDF(Student student, Course course) {
        Certificate cert = new Certificate(course.getCourseId(), student.getId());

        try {
            // تنظيف اسم الكورس من أي رموز قد تسبب مشاكل
            String safeCourseTitle = course.getTitle().replaceAll("[^a-zA-Z0-9_\\- ]", "");
            String defaultFileName = "Certificate_" + student.getUsername() + "_" + safeCourseTitle + ".pdf";

            // اختر مكان الحفظ
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(defaultFileName));
            int option = fileChooser.showSaveDialog(null);
            if(option != JFileChooser.APPROVE_OPTION) return cert;

            File file = fileChooser.getSelectedFile();
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 16);

            Paragraph title = new Paragraph("Certificate of Completion", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("This is to certify that", textFont));
            document.add(new Paragraph(student.getUsername(), titleFont));
            document.add(new Paragraph("has successfully completed the course:", textFont));
            document.add(new Paragraph(course.getTitle(), titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Issued on: " + cert.getIssueDate(), textFont));

            document.close();
            JOptionPane.showMessageDialog(null, "Certificate PDF generated at:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error generating certificate PDF:\n" + e.getMessage());
        }

        return cert;
    }
}
