package fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListStudentMakeMistakeRequest {

    private String examShiftCode;

    private String studentId;

}
