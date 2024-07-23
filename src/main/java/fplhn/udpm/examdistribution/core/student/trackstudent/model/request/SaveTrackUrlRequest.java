package fplhn.udpm.examdistribution.core.student.trackstudent.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SaveTrackUrlRequest {

    private String email;

    private String roomCode;

    private String url;

    private Boolean isDisable;

}
