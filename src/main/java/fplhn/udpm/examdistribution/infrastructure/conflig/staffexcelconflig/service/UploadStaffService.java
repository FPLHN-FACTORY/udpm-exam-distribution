package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface UploadStaffService {

    void init();

    String save(MultipartFile file);

    Resource load(String filename);

    void deleteAll();

    Stream<Path> loadAll();

    ResponseObject<Optional<String>> getExtensionByStringHandling(String filename);

}
