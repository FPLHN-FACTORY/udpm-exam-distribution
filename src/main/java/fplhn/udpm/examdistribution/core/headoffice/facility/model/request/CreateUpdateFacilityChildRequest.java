package fplhn.udpm.examdistribution.core.headoffice.facility.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateFacilityChildRequest {

    @NotBlank(message = "Tên cơ sở con không được để trống")
    @Size(min = 3, message = "Tên cơ sở con phải có ít nhất 5 kí tự")
    @Size(max = 50, message = "Tên cơ sở con phải ít hơn 50 kí tự")
    private String facilityChildName;

    @NotBlank(message = "Mã cơ sở con không được để trống")
    @Size(min = 2, message = "Mã cơ sở con phải có ít nhất 2 kí tự")
    @Size(max = 50, message = "Mã cơ sở con phải ít hơn 50 kí tự")
    private String facilityChildCode;

}
