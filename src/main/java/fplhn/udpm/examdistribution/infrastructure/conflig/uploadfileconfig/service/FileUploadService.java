package fplhn.udpm.examdistribution.infrastructure.conflig.uploadfileconfig.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileUploadService {

    void init();

    String save(MultipartFile file);

    Resource load(String filename);

    void deleteAll();

    Stream<Path> loadAll();

}
