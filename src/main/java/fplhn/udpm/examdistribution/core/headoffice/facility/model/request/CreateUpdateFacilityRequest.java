package fplhn.udpm.examdistribution.core.headoffice.facility.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateFacilityRequest {

    @NotBlank(message = "Tên cơ sở không được để trống")
    @Size(min = 3, message = "Tên cơ sở phải có ít nhất 5 kí tự")
    @Size(max = 50, message = "Tên cơ sở phải ít hơn 50 kí tự")
    private String facilityName;

}
