package fplhn.udpm.examdistribution.core.teacher.examshift.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TJoinExamShiftRequest {

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String examShiftCodeJoin;

}
