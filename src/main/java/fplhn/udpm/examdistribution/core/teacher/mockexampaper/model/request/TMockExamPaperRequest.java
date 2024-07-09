package fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TMockExamPaperRequest {

    private String idSubject;

    private String codeAndTeacher;

    private Long startDate;

    private Long endDate;

}
