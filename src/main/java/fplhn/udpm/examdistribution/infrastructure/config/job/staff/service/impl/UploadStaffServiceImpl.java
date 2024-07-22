package fplhn.udpm.examdistribution.infrastructure.config.job.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.service.UploadStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UploadStaffServiceImpl implements UploadStaffService {

    @Value("${file.upload.staff.path}")
    public String FILE_UPLOAD_STAFF_PATH;

    private Path root;

    @PostConstruct
    public void init() {
        root = Paths.get(FILE_UPLOAD_STAFF_PATH);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!", e);
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            if (file.getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                throw new RuntimeException(GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE);
            }
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
            String newFileName = generateUUIDFromTimestamp(timestamp) + fileExtension;
            Files.copy(file.getInputStream(), this.root.resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }

    private String generateUUIDFromTimestamp(Timestamp timestamp) {
        UUID uuid = UUID.nameUUIDFromBytes(ByteBuffer.allocate(16).putLong(timestamp.getTime()).array());
        return uuid.toString();
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try (Stream<Path> paths = Files.walk(this.root, 1)) {
            return paths.filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public ResponseObject<Optional<String>> getExtensionByStringHandling(String filename) {
        return new ResponseObject<>(Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)), HttpStatus.OK, "get extension successfully");
    }

}
