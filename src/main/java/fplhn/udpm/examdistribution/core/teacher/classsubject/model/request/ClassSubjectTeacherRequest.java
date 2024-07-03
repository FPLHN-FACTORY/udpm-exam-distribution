package fplhn.udpm.examdistribution.core.teacher.classsubject.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSubjectTeacherRequest {

    private String classSubjectCode;

    private String subjectId;

    private String blockId;

    private String facilityChildId;

}
