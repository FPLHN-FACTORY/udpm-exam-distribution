package fplhn.udpm.examdistribution.core.headoffice.subject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectRequest extends PageableRequest {

    private String subjectCode;

    private String subjectName;

    private String departmentId;

    private String subjectType;

    private Long startDate;

}
