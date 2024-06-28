package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MajorFacilityRequest extends PageableRequest {

    private String headMajorName;

    private String majorName;

    private String headMajorCode;

    @NotNull
    private String departmentFacilityId;

}
