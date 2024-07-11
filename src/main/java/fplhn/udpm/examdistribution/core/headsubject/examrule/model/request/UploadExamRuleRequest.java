package fplhn.udpm.examdistribution.core.headsubject.examrule.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadExamRuleRequest {

    private MultipartFile file;

}
