package fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSubjectByStaffRequest {

    private String staffId;

    private String subjectId;

    private String blockId;

}
