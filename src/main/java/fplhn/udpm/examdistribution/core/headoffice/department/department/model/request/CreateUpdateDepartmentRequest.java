package fplhn.udpm.examdistribution.core.headoffice.department.department.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateDepartmentRequest {

    @NotBlank(message = "Tên bộ môn không được để trống")
    private String departmentName;

    @NotBlank(message = "Mã bộ môn không được để trống")
    private String departmentCode;

}
