package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AddExamPaperRequest {

    @NotBlank(message = "Kiểu của đề thi chưa được chọn")
    private String examPaperType;

    @NotBlank(message = "Chuyên ngành cơ sở chưa được chọn")
    private String majorFacilityId;

    @NotBlank(message = "Môn học chưa được chọn")
    private String subjectId;

    private MultipartFile file;

}
