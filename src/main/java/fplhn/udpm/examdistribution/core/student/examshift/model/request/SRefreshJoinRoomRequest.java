package fplhn.udpm.examdistribution.core.student.examshift.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SRefreshJoinRoomRequest {

    private String studentId;

    private String examShiftCode;

}
