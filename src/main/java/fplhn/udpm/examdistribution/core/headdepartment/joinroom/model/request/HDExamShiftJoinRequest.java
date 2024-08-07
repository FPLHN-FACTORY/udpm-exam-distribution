package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HDExamShiftJoinRequest {

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

}
