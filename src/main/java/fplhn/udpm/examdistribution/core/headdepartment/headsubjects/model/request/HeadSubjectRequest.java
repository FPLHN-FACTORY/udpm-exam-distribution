package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HeadSubjectRequest extends PageableRequest {

    private String currentSemesterId;

    private String currentUserId;

    private String currentFacilityId;

    private String headSubjectRoleCode;

    private String currentDepartmentFacilityId;

}
