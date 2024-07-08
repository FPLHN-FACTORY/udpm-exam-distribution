package fplhn.udpm.examdistribution.core.teacher.examshift.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinExamShiftRequest {

    private String secondSupervisorId;

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordJoin;

}