package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSubjectGroupRequest extends PageableRequest {

    private String attachRoleName;

    @NotNull
    private String departmentFacilityId;

    @NotNull
    private String semesterId;

}
