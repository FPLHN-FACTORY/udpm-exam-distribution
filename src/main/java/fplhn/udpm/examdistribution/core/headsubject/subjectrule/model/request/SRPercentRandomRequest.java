package fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SRPercentRandomRequest {

    private String subjectId;

    private Long percentRandom;

}
