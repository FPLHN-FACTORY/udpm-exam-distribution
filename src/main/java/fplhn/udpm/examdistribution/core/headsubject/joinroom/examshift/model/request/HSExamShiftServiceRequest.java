package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HSExamShiftServiceRequest {

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

}
