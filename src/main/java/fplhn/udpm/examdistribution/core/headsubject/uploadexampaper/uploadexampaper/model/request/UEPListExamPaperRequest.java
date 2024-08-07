package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UEPListExamPaperRequest extends PageableRequest {

    private String subjectId;

    private String staffId;

    private String examPaperType;

}
