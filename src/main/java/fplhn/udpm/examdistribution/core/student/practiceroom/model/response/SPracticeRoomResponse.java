package fplhn.udpm.examdistribution.core.student.practiceroom.model.response;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SPracticeRoomResponse {

    private String practiceRoomId;

    private String practiceRoomCode;

    private String password;

    private Boolean isTakeNewMockExamPaper;

    private String key;

    private FileResponse file;
}
