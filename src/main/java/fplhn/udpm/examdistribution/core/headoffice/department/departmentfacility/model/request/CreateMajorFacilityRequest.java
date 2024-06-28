package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMajorFacilityRequest {

    @NotNull(message = "Vui lòng chọn chuyên ngành")
    private String majorId;

    @NotNull(message = "Vui lòng chọn trưởng môn chuyên ngành")
    private String headMajorId;

    @NotNull(message = "ID bộ môn theo cơ sở không được để trống")
    private String departmentFacilityId;

}
