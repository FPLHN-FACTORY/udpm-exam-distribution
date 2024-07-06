package fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindStaffRequest extends PageableRequest {

    private String accountFptOrFe;

    private String staffCode;

    private String staffName;

    private String subjectId;

}
