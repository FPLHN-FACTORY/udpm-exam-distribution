package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifySubjectGroupRequest {

    @NotNull(message = "Danh sách môn học không được để trống")
    private String[] attachSubjectIds;

    @NotNull(message = "Danh sách môn học không được để trống")
    private String[] detachSubjectIds;

    @NotNull(message = "Tên chức vụ không được để trống")
    private String attachRoleName;

}
