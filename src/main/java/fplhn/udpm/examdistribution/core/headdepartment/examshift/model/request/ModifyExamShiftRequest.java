package fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyExamShiftRequest {

    private String firstSupervisorCode;

    private String secondSupervisorCode;

    private Long examDate;

    private String room;

    private String shift;

}
