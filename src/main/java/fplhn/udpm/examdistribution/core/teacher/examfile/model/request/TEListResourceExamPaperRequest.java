package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TEListResourceExamPaperRequest extends PageableRequest {

    private String examPaperId;

}
