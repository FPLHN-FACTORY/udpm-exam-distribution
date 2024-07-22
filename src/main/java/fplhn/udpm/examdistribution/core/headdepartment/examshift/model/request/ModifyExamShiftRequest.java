package fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyExamShiftRequest {

    private String firstSupervisorId;

    private String secondSupervisorId;

    private Long examDate;

    private String room;

    private String shift;

}
