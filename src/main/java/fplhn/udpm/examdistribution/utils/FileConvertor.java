package fplhn.udpm.examdistribution.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class FileConvertor {

    public byte[] convertDocxToPdf(MultipartFile file) throws IOException {
        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document pdfDoc = new Document(pdf);

        for (XWPFParagraph paragraph : doc.getParagraphs()) {
            System.out.println(paragraph.getText());
            pdfDoc.add(new Paragraph(paragraph.getText()));
        }

        pdfDoc.close();
        doc.close();
        return out.toByteArray();
    }

}
