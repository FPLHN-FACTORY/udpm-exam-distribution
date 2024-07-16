package fplhn.udpm.examdistribution.core.student.trackstudent.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckRoomIsValidRequest {

    private String email;

    private String roomCode;

}
