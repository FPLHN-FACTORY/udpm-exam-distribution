//package fplhn.udpm.examdistribution.utils;
//
//import com.documents4j.api.DocumentType;
//import com.documents4j.api.IConverter;
//import com.documents4j.job.LocalConverter;
//import org.springframework.stereotype.Component;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//@Component
//public class FileConvertor {
//
//    public byte[] convertDocxToPdf(InputStream docxInputStream) throws IOException {
//        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
//
//        IConverter converter = LocalConverter.builder().build();
//        converter.convert(docxInputStream).as(DocumentType.DOCX)
//                .to(pdfOutputStream).as(DocumentType.PDF)
//                .execute();
//
//        return pdfOutputStream.toByteArray();
//    }
//
//}