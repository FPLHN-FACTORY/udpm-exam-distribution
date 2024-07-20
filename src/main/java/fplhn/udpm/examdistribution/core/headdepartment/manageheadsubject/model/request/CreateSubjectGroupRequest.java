package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSubjectGroupRequest {

    @NotNull(message = "Tên chức vụ không được để trống")
    @NotBlank(message = "Tên chức vụ không được để trống")
    private String attachRoleName;

}
