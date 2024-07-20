package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSubjectGroupRequest extends PageableRequest {

    private String attachRoleName;

    private String departmentFacilityId;

    private String semesterId;

}
