package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CEPListExamPaperRequest extends PageableRequest {

    private String semesterId;

    private String blockId;

    private String subjectId;

    private String staffId;

    private String examPaperType;

}
