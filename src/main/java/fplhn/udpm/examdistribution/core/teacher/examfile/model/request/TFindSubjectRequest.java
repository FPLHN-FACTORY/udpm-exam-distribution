package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TFindSubjectRequest extends PageableRequest {

    private String subjectCode;

    private String subjectName;

    private String staffId;

}
