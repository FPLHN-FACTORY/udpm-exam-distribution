package fplhn.udpm.examdistribution.core.teacher.examshift.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TCreateExamShiftRequest {

    @NotBlank(message = "Vui lòng nhập đầy đủ thông tin để lấy lớp môn")
    private String classSubjectId;

    private String firstSupervisorId;

    private Long examDate;

    private String shift;

    @NotBlank(message = "Phòng thi không được để trống")
    private String room;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

}
