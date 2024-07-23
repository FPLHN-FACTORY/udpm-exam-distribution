package fplhn.udpm.examdistribution.infrastructure.config.drive.service.impl;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveManagerService;
import fplhn.udpm.examdistribution.infrastructure.config.drive.utils.PermissionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleDriveFileServiceImpl implements GoogleDriveFileService {

    private final GoogleDriveManagerService googleDriveManagerService;

    private final Drive googleDrive;

    @Override
    public List<GoogleDriveFileDTO> findAll() {

        List<GoogleDriveFileDTO> googleDriveFileDTOS = new ArrayList<>();
        List<File> files = googleDriveManagerService.findAll();

        if (files == null) return googleDriveFileDTOS;

        files.forEach(file -> {
            if (file.getSize() != null) {
                GoogleDriveFileDTO driveFileDto = new GoogleDriveFileDTO();
                fillGoogleDriveFileDTOList(googleDriveFileDTOS, file, driveFileDto);
            }
        });

        return googleDriveFileDTOS;
    }

    @Override
    public List<GoogleDriveFileDTO> findAllInFolder(String folderId) {

        List<GoogleDriveFileDTO> googleDriveFileDTOList = new ArrayList<>();
        List<File> files = googleDriveManagerService.findAllFilesInFolderById(folderId);

        if (files == null) return googleDriveFileDTOList;

        files.forEach(file -> {
            if (file.getSize() != null) {
                GoogleDriveFileDTO driveFileDto = new GoogleDriveFileDTO();
                fillGoogleDriveFileDTOList(googleDriveFileDTOList, file, driveFileDto);
            }
        });

        return googleDriveFileDTOList;
    }

    private void fillGoogleDriveFileDTOList(List<GoogleDriveFileDTO> googleDriveFileDTOS, File file, GoogleDriveFileDTO driveFileDto) {
        driveFileDto.setId(file.getId());
        driveFileDto.setName(file.getName());
        driveFileDto.setThumbnailLink(file.getThumbnailLink());
        driveFileDto.setSize(String.valueOf(file.getSize()));
        driveFileDto.setLink("https://drive.google.com/file/d/" + file.getId() + "/view?usp=sharing");
        driveFileDto.setShared(file.getShared());
        googleDriveFileDTOS.add(driveFileDto);
    }

    @Override
    public void deleteById(String fileId) {
        googleDriveManagerService.deleteFileOrFolderById(fileId);
    }

    @Override
    public GoogleDriveFileDTO upload(MultipartFile file, String folderName, boolean isPublic) {
        PermissionDetail permissionDetail = PermissionDetail.builder().build();

        if (isPublic) {
            permissionDetail.setType("anyone");
            permissionDetail.setRole("reader");
        } else {
            permissionDetail.setType("private");
            permissionDetail.setRole("private");
        }
        permissionDetail.setEmailAddress("exam-distribution@exam-distribution-428417.iam.gserviceaccount.com");

        return googleDriveManagerService.uploadFile(file, folderName, permissionDetail);
    }

    @Override
    public void download(String fileId, OutputStream outputStream) {
        googleDriveManagerService.download(fileId, outputStream);
    }

    @Override
    public void copyToFolder(String fileId, String folderId) {
        googleDriveManagerService.copy(fileId, folderId);
    }

    @Override
    public void moveToFolder(String fileId, String folderId) {
        googleDriveManagerService.move(fileId, folderId);
    }

    @Override
    public void shareFile(String fileId, String gmail) {
        PermissionDetail permissionDetail = PermissionDetail
                .builder()
                .emailAddress(gmail)
                .type("user")
                .role("reader")
                .build();

        googleDriveManagerService.createPermissionForEmail(fileId, permissionDetail);
    }

    @Override
    public Resource loadFile(String fileId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            googleDrive.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not download the file!", e);
        }
        return new ByteArrayResource(outputStream.toByteArray());
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        try {
//            // Get file metadata to determine its MIME type
//            File fileMetadata = googleDrive.files().get(fileId).execute();
//            String mimeType = fileMetadata.getMimeType();
//
//            // Download the file
//            googleDrive.files().get(fileId).executeMediaAndDownloadTo(outputStream);
//
//            if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) {
//                // Convert DOCX to PDF
//                try (InputStream docxInputStream = new ByteArrayInputStream(outputStream.toByteArray())) {
//                    XWPFDocument document = new XWPFDocument(docxInputStream);
//                    OutputStream pdfOutputStream = new FileOutputStream("output.pdf");
//                    Document pdfDocument = new Document();
//                    PdfWriter.getInstance(pdfDocument, pdfOutputStream);
//                    pdfDocument.open();
//
//                    List<XWPFParagraph> paragraphs = document.getParagraphs();
//                    for (XWPFParagraph paragraph : paragraphs) {
//                        pdfDocument.add(new Paragraph(paragraph.getText()));
//                    }
////                    ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
////                    PdfConverter.getInstance().convert(document, pdfOutputStream, Options.getDefault());
////                    return new ByteArrayResource(pdfOutputStream.toByteArray());
//                }
//            } else if ("application/pdf".equals(mimeType)) {
//                // Return the PDF file as is
//                return new ByteArrayResource(outputStream.toByteArray());
//            } else {
//                throw new RuntimeException("Unsupported file type: " + mimeType);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Could not download or process the file!", e);
//        }
    }

    @Override
    public String getFileName(String fileId) {
        return googleDriveManagerService.getFileName(fileId);
    }

}
