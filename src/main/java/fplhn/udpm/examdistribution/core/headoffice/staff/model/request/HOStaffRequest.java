package fplhn.udpm.examdistribution.core.headoffice.staff.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HOStaffRequest extends PageableRequest {

    private String searchQuery;

}
