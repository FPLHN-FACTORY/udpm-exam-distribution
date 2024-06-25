package fplhn.udpm.examdistribution.core.headoffice.department.department.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CreateUpdateDepartmentRequest {

    @NotBlank(message = "Tên bộ môn không được để trống")
    @Length(max = 50,message = "Tên của bộ môn không được dài quá 50 ký tự")
    private String departmentName;

    @NotBlank(message = "Mã bộ môn không được để trống")
    @Length(max = 50,message = "Mã của bộ môn không được dài quá 50 ký tự")
    private String departmentCode;

}
