package fplhn.udpm.examdistribution.core.headsubject.examrule.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadExamRuleRequest {

    private MultipartFile file;

    @NotBlank(message = "Tên quy định thi chưa được tải")
    private String name;

}
