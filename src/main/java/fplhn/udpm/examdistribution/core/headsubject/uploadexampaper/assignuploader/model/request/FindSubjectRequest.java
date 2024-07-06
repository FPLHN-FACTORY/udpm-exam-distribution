package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindSubjectRequest extends PageableRequest {

    private String subjectCode;

    private String subjectName;

    private String staffId;

}
