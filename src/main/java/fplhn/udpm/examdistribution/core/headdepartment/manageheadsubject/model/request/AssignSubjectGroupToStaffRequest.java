package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AssignSubjectGroupToStaffRequest {

    @NotNull(message = "Nhân viên không được để trống")
    private String staffId;

    @NotNull(message = "Chức vụ nhóm môn học cần phân công không được để trống")
    private String subjectGroupId;

    private String semesterId;

}
