package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CreateSampleExamPaperRequest {

    private String subjectId;

    private MultipartFile file;

    private String majorFacilityId;

}
