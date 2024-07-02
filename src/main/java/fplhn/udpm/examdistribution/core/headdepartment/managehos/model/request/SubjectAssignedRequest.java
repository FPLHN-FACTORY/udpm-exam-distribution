package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectAssignedRequest extends PageableRequest {

    @NotNull
    private String staffId;

    @NotNull
    private String departmentFacilityId;

    private String subjectCode;

    private String subjectName;

    private String subjectType;

}
