package fplhn.udpm.examdistribution.core.headoffice.department.department.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindMajorRequest extends PageableRequest {

    private String majorName;

}
