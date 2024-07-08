package fplhn.udpm.examdistribution.core.student.home.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentExamShiftRequest {

    private String studentId;

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordJoin;

    private Long joinTime;

}
