package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SubjectByHeadSubjectRequest extends PageableRequest {

    private String currentSemesterId;

    private String headSubjectId;

    private String departmentFacilityId;

    private String facilityId;

}
