package fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountClassSubjectByStaffRequest {

    private String staffId;

    private String subjectId;

    private String blockId;

}
