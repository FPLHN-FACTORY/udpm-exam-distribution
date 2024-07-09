package fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TSubjectMockExamRequest extends PageableRequest {

    private String subjectAndDepartment;

    private String subjectType;

    private String subjectStatus;

    private String semester;

}
