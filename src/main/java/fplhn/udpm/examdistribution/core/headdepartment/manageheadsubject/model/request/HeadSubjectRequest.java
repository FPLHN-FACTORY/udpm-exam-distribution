package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeadSubjectRequest extends PageableRequest {

    @NotNull
    private String departmentFacilityId;

    private String semesterId;

    @NotNull
    private String currentUserId;

}
