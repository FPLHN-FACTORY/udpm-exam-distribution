package fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HSExamShiftRequest {

    private String departmentFacilityId;

    private String semesterId;

    private String staffId;

    private Long currentDate;

    private String currentShift;
}
