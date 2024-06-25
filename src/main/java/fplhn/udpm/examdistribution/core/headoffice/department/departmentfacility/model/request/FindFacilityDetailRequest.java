package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindFacilityDetailRequest extends PageableRequest {

    private String facilityName;

}
