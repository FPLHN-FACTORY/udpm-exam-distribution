package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectGroupAssignedRequest extends PageableRequest {

    @NotNull
    private String staffId;

    private String attachRoleName;

    private String currentUserId;

    private String departmentFacilityId;

    private String semesterId;

}
