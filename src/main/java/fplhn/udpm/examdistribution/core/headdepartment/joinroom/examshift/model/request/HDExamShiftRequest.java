package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HDExamShiftRequest {

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordJoin;

}
