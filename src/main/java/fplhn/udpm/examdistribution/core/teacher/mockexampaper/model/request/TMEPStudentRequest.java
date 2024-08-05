package fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TMEPStudentRequest extends PageableRequest {

    private String keyWord;

    private String practiceRoomId;

}
