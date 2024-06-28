package fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSubjectKeywordRequest extends PageableRequest {

    private String keyword;

    private String facilityChildId;

    private Long startDate;

}
