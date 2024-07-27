package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import fplhn.udpm.examdistribution.infrastructure.constant.SubjectType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TFindSubjectRequest extends PageableRequest {

    private String findSubject;

    private String subjectType;

}
