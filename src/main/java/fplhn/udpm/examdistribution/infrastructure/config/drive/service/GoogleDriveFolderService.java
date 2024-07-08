package fplhn.udpm.examdistribution.infrastructure.config.drive.service;

import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFolderDTO;

import java.util.List;

public interface GoogleDriveFolderService {

    List<GoogleDriveFolderDTO> findAll();

    String create(String folderName, String parentId);

    String getFolderId(String folderName);

    void delete(String id);

    byte[] download(String folderId);

    void moveFolderToAnother(String fromFolderId, String toFolderId);

    void copyFolderToAnother(String fromId, String toId);

    void shareFolder(String folderId, String gmail);

}
