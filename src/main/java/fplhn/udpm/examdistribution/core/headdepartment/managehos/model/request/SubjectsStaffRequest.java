package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectsStaffRequest extends PageableRequest {

    @NotNull(message = "ID bộ môn theo cơ sở không được để trống")
    private String departmentFacilityId;

    private String subjectCode;

    private String subjectName;

    private String staffCode;

    private String staffName;

}
