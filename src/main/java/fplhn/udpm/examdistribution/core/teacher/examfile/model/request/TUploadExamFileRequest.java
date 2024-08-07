package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class TUploadExamFileRequest {

    private MultipartFile file;

    private String contentFile;

}
