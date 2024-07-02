package fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AssignSubjectStaffRequest {

    @NotNull(message = "Nhân viên không được để trống")
    private String staffId;

    @NotNull(message = "Môn học cần phân công không được để trống")
    private String[] assignedSubjectIds;

    @NotNull(message = "Môn học gỡ phân công không được để trống")
    private String[] unassignedSubjectIds;

}
