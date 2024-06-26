package fplhn.udpm.examdistribution.core.headoffice.facility.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUpdateFacilityChildRequest {

    @NotBlank(message = "Tên cơ sở con không được để trống")
    private String facilityChildName;

}
