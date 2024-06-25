package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateOrUpdateDepartmentFacilityRequest {

    @NotBlank(message = "Bộ môn không được để trống")
    private String departmentId;

    @NotBlank(message = "Cơ sở không được để trống")
    private String facilityId;

    @NotBlank(message = "CNBM không được để trống")
    private String headOfDepartmentId;

}
