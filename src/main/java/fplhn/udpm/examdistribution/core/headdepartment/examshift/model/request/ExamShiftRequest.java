package fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamShiftRequest extends PageableRequest {

    private String shift;

    private String blockId;

    private String staffCode;

    private String room;

    private String classSubjectCode;

    private String subjectCode;

}
