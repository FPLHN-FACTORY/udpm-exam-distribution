package fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service.impl;

import com.google.api.services.drive.model.File;
import com.google.common.io.ByteStreams;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.dto.GoogleDriveFolderDTO;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service.GoogleDriveFolderService;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.service.GoogleDriveManagerService;
import fplhn.udpm.examdistribution.infrastructure.conflig.googledrive.utils.PermissionDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class GoogleDriveFolderServiceImpl implements GoogleDriveFolderService {

    private final GoogleDriveManagerService googleDriveManagerService;

    @Override
    public List<GoogleDriveFolderDTO> findAll() {
        List<File> folders = googleDriveManagerService.findAllFilesInFolderById("root");
        List<GoogleDriveFolderDTO> googleDriveFolderDTOS = new ArrayList<>();

        if (folders == null) return googleDriveFolderDTOS;

        folders.forEach(folder -> {
            if (folder.getSize() == null) {
                GoogleDriveFolderDTO dto = new GoogleDriveFolderDTO();
                dto.setId(folder.getId());
                dto.setName(folder.getName());
                dto.setLink("https://drive.google.com/drive/u/0/folders/" + folder.getId());
                googleDriveFolderDTOS.add(dto);
            }
        });

        return googleDriveFolderDTOS;
    }

    @Override
    public String create(String folderName, String parentId) {
        return googleDriveManagerService.createFolder(folderName, parentId);
    }

    @Override
    public String getFolderId(String folderName) {
        return googleDriveManagerService.getFolderId(folderName);
    }

    @Override
    public void delete(String id) {
        googleDriveManagerService.deleteFileOrFolderById(id);
    }

    @Override
    public byte[] download(String folderId) {
        List<File> files = googleDriveManagerService.findAllFilesInFolderById(folderId);
        return zipFiles(files);
    }

    private byte[] zipFiles(List<File> files) {

        byte[] result;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (File file : files) {
                try (InputStream fileInputStream = googleDriveManagerService.getFileAsInputStream(file.getId())) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    ByteStreams.copy(fileInputStream, zipOutputStream);
                }
            }

            zipOutputStream.close();
            byteArrayOutputStream.close();
            result = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public void moveFolderToAnother(String fromFolderId, String toFolderId) {
        googleDriveManagerService.move(fromFolderId, toFolderId);
    }

    @Override
    public void copyFolderToAnother(String fromId, String toId) {
        File fromFolder = googleDriveManagerService.findFolderById(fromId);

        File newFolder = new File();
        newFolder.setName(fromFolder.getName());
        newFolder.setParents(List.of(toId));
        newFolder.setMimeType("application/vnd.google-apps.folder");

        List<File> folders = googleDriveManagerService.findAllFoldersInFolderById(fromId);
        List<File> files = googleDriveManagerService.findAllFilesInFolderById(fromId);
        files.forEach(file -> googleDriveManagerService.copy(file.getId(), newFolder.getId()));
        folders.forEach(folder -> copyFolderToAnother(folder.getId(), newFolder.getId()));
    }

    @Override
    public void shareFolder(String folderId, String gmail) {
        PermissionDetail permissionDetail = PermissionDetail
                .builder()
                .emailAddress(gmail)
                .type("user")
                .role("reader")
                .build();

        googleDriveManagerService.createPermissionForEmail(folderId, permissionDetail);
    }

}
