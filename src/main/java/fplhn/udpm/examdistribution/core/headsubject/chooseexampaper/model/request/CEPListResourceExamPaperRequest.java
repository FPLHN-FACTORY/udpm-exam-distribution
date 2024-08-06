package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CEPListResourceExamPaperRequest extends PageableRequest {

    private String examPaperId;

}
