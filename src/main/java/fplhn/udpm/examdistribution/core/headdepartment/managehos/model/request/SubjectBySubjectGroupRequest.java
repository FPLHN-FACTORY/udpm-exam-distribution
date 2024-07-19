package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectBySubjectGroupRequest extends PageableRequest {

    private String subjectGroupId;

    private String departmentFacilityId;

}
