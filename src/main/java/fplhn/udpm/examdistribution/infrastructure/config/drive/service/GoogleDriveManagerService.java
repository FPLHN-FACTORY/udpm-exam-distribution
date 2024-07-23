package fplhn.udpm.examdistribution.infrastructure.config.drive.service;

import com.google.api.services.drive.model.File;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.utils.PermissionDetail;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface GoogleDriveManagerService {

    List<File> findAll();

    List<File> findAllFilesInFolderById(String folderId);

    List<File> findAllFoldersInFolderById(String folderId);

    void download(String fileId, OutputStream outputStream);

    void createPermissionForEmail(String id, PermissionDetail permissionDetail);

    GoogleDriveFileDTO uploadFile(MultipartFile multipartFile, String folderName, PermissionDetail permissionDetail);

    String getFolderId(String folderName);

    File findFolderById(String folderId);

    void deleteFileOrFolderById(String id);

    InputStream getFileAsInputStream(String fileID);

    void copy(String fileId, String folderId);

    void move(String fileId, String folderId);

    String createFolder(String folderName, String parentId);

    String getFileName(String fileId);

}
