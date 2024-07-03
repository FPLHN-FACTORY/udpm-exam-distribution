package fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service;

import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.dto.GoogleDriveFileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

public interface GoogleDriveFileService {

    List<GoogleDriveFileDTO> findAll();

    List<GoogleDriveFileDTO> findAllInFolder(String folderId);

    void deleteById(String fileId);

    String upload(MultipartFile file, String folderName, boolean isPublic);

    void download(String fileId, OutputStream outputStream);

    void copyToFolder(String fileId, String folderId);

    void moveToFolder(String fileId, String folderId);

    void shareFile(String fileId, String gmail);

}
