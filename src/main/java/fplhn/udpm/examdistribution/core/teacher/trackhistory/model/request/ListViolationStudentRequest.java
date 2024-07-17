package fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListViolationStudentRequest extends PageableRequest {

    private String examShiftCode;

    private String studentId;

}
