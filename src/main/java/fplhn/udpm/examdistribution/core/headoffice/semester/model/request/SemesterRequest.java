package fplhn.udpm.examdistribution.core.headoffice.semester.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SemesterRequest extends PageableRequest {

    private String semesterName;

    private Long semesterYear;

    private Long startDate;

}
