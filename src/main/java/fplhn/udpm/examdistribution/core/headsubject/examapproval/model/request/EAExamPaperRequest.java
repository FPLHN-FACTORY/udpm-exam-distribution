package fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EAExamPaperRequest extends PageableRequest {

    private String idSubject;

    private String staffUploadCode;

}
