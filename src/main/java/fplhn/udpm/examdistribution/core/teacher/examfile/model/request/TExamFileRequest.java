package fplhn.udpm.examdistribution.core.teacher.examfile.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TExamFileRequest extends PageableRequest {

    private String idSubject;

    private String codeAndTeacher;

    private Long startDate;

    private Long endDate;

    private String examPaperStatus;

}
