package fplhn.udpm.examdistribution.core.headoffice.facility.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateFacilityRequest {

    @NotBlank(message = "Tên cơ sở không được để trống")
    private String facilityName;

}
