package fplhn.udpm.examdistribution.core.student.practiceroom.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPracticeRoomRequest {

    private String studentId;

    @NotBlank(message = "Mã phòng thi không được để trống")
    private String practiceRoomCodeJoin;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String passwordJoin;

    private Boolean isTakeNewMockExamPaper;

    private String key;

}
