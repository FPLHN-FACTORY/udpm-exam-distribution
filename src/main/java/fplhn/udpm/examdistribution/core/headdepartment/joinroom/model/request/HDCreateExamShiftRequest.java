package fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HDCreateExamShiftRequest {

    @NotBlank(message = "Vui lòng nhập đầy đủ thông tin để lấy lớp môn")
    private String classSubjectId;

    @NotBlank(message = "Giám thị 1 không được để trống")
    private String firstSupervisorCode;

    @NotBlank(message = "Giám thị 2 không được để trống")
    private String secondSupervisorCode;

    private Long examDate;

    private String shift;

    @NotBlank(message = "Phòng thi không được để trống")
    private String room;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

}
