package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CEPCreateExamPaperRequest {

    @NotBlank(message = "Kiểu của đề thi chưa được chọn")
    private String examPaperType;

    @NotBlank(message = "Môn học chưa được chọn")
    private String subjectId;

    private List<MultipartFile> file;

}
