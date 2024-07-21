package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HOStaffMajorFacilityRequest {

    private String idStaffMajorFacility;

    @NotEmpty(message = "Id staff không được trống")
    @NotNull(message = "Id staff không được trống")
    private String idStaff;

    @NotEmpty(message = "Cơ sở không được trống")
    @NotNull(message = "Cơ sở không được trống")
    private String idFacility;

    @NotEmpty(message = "Bộ môn không được trống")
    @NotNull(message = "Bộ môn không được trống")
    private String idDepartment;

    @NotEmpty(message = "Chuyên ngành không được trống")
    @NotNull(message = "Chuyên ngành không được trống")
    private String idMajor;

}
