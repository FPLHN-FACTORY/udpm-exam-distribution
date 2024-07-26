package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HDExamShiftRequest {

    private String departmentFacilityId;

    private String semesterId;

    private Long currentDate;

    private String currentShift;
}
